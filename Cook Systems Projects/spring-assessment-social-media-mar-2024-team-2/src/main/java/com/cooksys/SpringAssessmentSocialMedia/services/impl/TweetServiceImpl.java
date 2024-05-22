package com.cooksys.SpringAssessmentSocialMedia.services.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.cooksys.SpringAssessmentSocialMedia.entities.Hashtag;
import com.cooksys.SpringAssessmentSocialMedia.repositories.HashtagRepository;
import org.springframework.stereotype.Service;

import com.cooksys.SpringAssessmentSocialMedia.dtos.ContextDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.CredentialsDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.HashtagDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetRequestDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.TweetResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.dtos.UserResponseDto;
import com.cooksys.SpringAssessmentSocialMedia.entities.Credentials;
import com.cooksys.SpringAssessmentSocialMedia.entities.Tweet;
import com.cooksys.SpringAssessmentSocialMedia.entities.User;
import com.cooksys.SpringAssessmentSocialMedia.exceptions.BadRequestException;
import com.cooksys.SpringAssessmentSocialMedia.exceptions.NotAuthorizedException;
import com.cooksys.SpringAssessmentSocialMedia.exceptions.NotFoundException;
import com.cooksys.SpringAssessmentSocialMedia.mappers.CredentialsMapper;
import com.cooksys.SpringAssessmentSocialMedia.mappers.TweetMapper;
import com.cooksys.SpringAssessmentSocialMedia.mappers.UserMapper;
import com.cooksys.SpringAssessmentSocialMedia.repositories.TweetRepository;
import com.cooksys.SpringAssessmentSocialMedia.repositories.UserRepository;
import com.cooksys.SpringAssessmentSocialMedia.services.TweetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService{

    
    private final TweetRepository tweetRepository;
    //private final TweetService tweetService;
    private final TweetMapper tweetMapper;
    private final CredentialsMapper credentialsMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final HashtagRepository hashtagRepository;

    private User getUserByUsername (String username) {
        Optional<User> maybeUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
        if (maybeUser.isEmpty()) {
            throw new NotFoundException("username does not match existing user");
        }

        return maybeUser.get();
    }

    private User getUserByCredentials (Credentials credentials) {
        Optional<User> maybeUser = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credentials.getUsername(), credentials.getPassword());
        if (maybeUser.isEmpty())
            throw new NotFoundException("credentials do not match existing user");

        return maybeUser.get();
    }
    private List<User> parseTweetMentions (Tweet tweet) {
        List<User> mentioned = new LinkedList<User>();
        String content = tweet.getContent();
        if (content != null) {
            String[] splitTweet = content.split(" ");
            for (String s : splitTweet) {
                if (splitTweet.length > 1 && s.charAt(0) == '@') {
                    String username = s.substring(1);
                    User user = getUserByUsername(username);
                    if (!mentioned.contains(user)) {
                        mentioned.add(user);
                    }
                }
            }
        }
        return mentioned;
    }

    private Hashtag getHashtag (String hashtagLabel, Tweet tweet) {
        Optional<Hashtag> maybeHashtag = hashtagRepository.findByLabel(hashtagLabel);
        Hashtag hashtag;
        if (maybeHashtag.isEmpty()) {
            hashtag = new Hashtag();
            hashtag.setLabel(hashtagLabel);
            List<Tweet> tweets = new LinkedList<>();
            tweets.add(tweet);
//            hashtag.setTweets(tweets);

        }
        else {
            hashtag = maybeHashtag.get();
        }

        return hashtagRepository.saveAndFlush(hashtag);
    }

    private List<Hashtag> parseTweetHashtags (Tweet tweet) {
        List<Hashtag> hashtags = new LinkedList<>();
        String content = tweet.getContent();
        if (content != null) {
            String[] splitTweet = content.split(" ");
            for (String s : splitTweet) {
                if (splitTweet.length > 1 && s.charAt(0) == '#') {
                    String hashtagLabel = s.substring(1);
                    Hashtag hashtag = getHashtag(hashtagLabel, tweet);
                    if (!hashtags.contains(hashtag)) {
                        hashtags.add(hashtag);
                    }
                }
            }
        }
        return hashtags;
    }

    private Tweet saveTweet (Tweet tweet, Credentials credentials) {
        System.out.println(tweet.getContent());
        tweet.setAuthor(getUserByCredentials(credentials));

        //TODO: implement tweet parsing to add mentions and hashtags
        List<User> mentionedUsers = parseTweetMentions(tweet);
        tweet.setMentionedUsers(mentionedUsers);
        tweet.setHashtags(parseTweetHashtags(tweet));
        Tweet newTweet = tweetRepository.saveAndFlush(tweet);

        for (User e : mentionedUsers) {
            e.getMentionedTweets().add(tweet);
        }
        userRepository.saveAllAndFlush(mentionedUsers);
        System.out.println(tweet.getMentionedUsers() == null);
        System.out.println(parseTweetMentions(tweet).size());


        return newTweet;

        
    }

    @Override
    public List<TweetResponseDto> getAllTweets () {
        return tweetMapper.entitiesToDtos(tweetRepository.findAllByDeletedFalseOrderByPostedDesc());
    }

    @Override
    public TweetResponseDto postTweet (TweetRequestDto request) {
        if (request.getCredentials() == null) {
            throw new NotAuthorizedException("You do not have any credentials to make a tweet");
        }
        if (request.getContent() == null) {
            throw new NotFoundException("You have no content in your tweet to post");
        }
        Credentials credentials = credentialsMapper.dtoToEntity(request.getCredentials());
        Tweet tweet = tweetMapper.dtoToEntity(request);
        return tweetMapper.entityToDto(saveTweet(tweet, credentials));
    }

    @Override
    public TweetResponseDto getTweetById(Long id) {
        Tweet tweet = tweetRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new NotFoundException("Tweet not found or deleted"));

        return tweetMapper.entityToDto(tweet);
    }

    @Override
    public ContextDto getTweetContextById(Long id) {
        Tweet targetTweet = tweetRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found or deleted"));

        List<Tweet> allReplies = tweetRepository.findByInReplyToId(id);

        List<Tweet> beforeTweets = new ArrayList<>();
        List<Tweet> afterTweets = new ArrayList<>();

        // Separate replies into before and after based on their timestamp
        for (Tweet reply : allReplies) {
            if (reply.getPosted().before(targetTweet.getPosted())) {
                beforeTweets.add(reply);
            } else {
                afterTweets.add(reply);
            }
        }

        // Sort before and after tweets by timestamp
        beforeTweets.sort(Comparator.comparing(Tweet::getPosted));
        afterTweets.sort(Comparator.comparing(Tweet::getPosted));

        List<TweetResponseDto> beforeDto = tweetMapper.entitiesToDtos(beforeTweets);
        List<TweetResponseDto> afterDto = tweetMapper.entitiesToDtos(afterTweets);

        ContextDto contextDto = new ContextDto();
        contextDto.setTarget(targetTweet != null ? tweetMapper.entityToDto(targetTweet) : null);
        contextDto.setBefore(beforeDto);
        contextDto.setAfter(afterDto);

        return contextDto;
    }

    private Tweet getTweet (Long id) {
        Optional<Tweet> tweet = tweetRepository.findByIdAndDeletedFalse(id);
        if (tweet.isEmpty()) {
            throw new NotFoundException("The tweet could not be found.");
        }
        return tweet.get();
    }

    private boolean credentialsMatch(Tweet tweet, Credentials credentials) {
//        System.out.println(tweet.getAuthor().getCredentials().getUsername());
//        System.out.println(tweet.getAuthor().getCredentials().getPassword());
//        System.out.println(credentials.getUsername());
//        System.out.println(credentials.getPassword());
        return tweet.getAuthor().getCredentials().getUsername().equals(credentials.getUsername())
                && tweet.getAuthor().getCredentials().getPassword().equals(credentials.getPassword());
    }
    @Override
    public TweetResponseDto deleteTweet(Long id, CredentialsDto credentials) {
        // Check if the tweet exists
        Tweet tweet = getTweet(id);
        Credentials cred = credentialsMapper.dtoToEntity(credentials);
        //System.out.println(cred);
        // Check if credentials match the author of the tweet
        if (!credentialsMatch(tweet, cred)) {
            throw new NotAuthorizedException("Unauthorized to delete this tweet");
        }

        // "Delete" the tweet by setting a flag
        tweet.setDeleted(true);

        return tweetMapper.entityToDto(tweetRepository.saveAndFlush(tweet));
    }

    private String processContent(String content) {
        // Process mentions
        Pattern mentionPattern = Pattern.compile("@\\{(\\w+)}");
        Matcher mentionMatcher = mentionPattern.matcher(content);
        StringBuilder mentionResult = new StringBuilder();
        while (mentionMatcher.find()) {
            String username = mentionMatcher.group(1);
            // Replace @{username} with appropriate mention format
            mentionMatcher.appendReplacement(mentionResult, "<a href='/users/" + username + "'>@" + username + "</a>");
        }
        mentionMatcher.appendTail(mentionResult);

        // Process hashtags
        Pattern hashtagPattern = Pattern.compile("#\\{(\\w+)}");
        Matcher hashtagMatcher = hashtagPattern.matcher(mentionResult.toString());
        StringBuilder hashtagResult = new StringBuilder();
        while (hashtagMatcher.find()) {
            String hashtag = hashtagMatcher.group(1);
            // Replace #{hashtag} with appropriate hashtag format
            hashtagMatcher.appendReplacement(hashtagResult, "<a href='/hashtags/" + hashtag + "'>#" + hashtag + "</a>");
        }
        hashtagMatcher.appendTail(hashtagResult);

        return hashtagResult.toString();
    }

    private User validateCredentials(Credentials credentials) {
        return userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credentials.getUsername(), credentials.getPassword())
                .orElseThrow(() -> new NotAuthorizedException("Invalid credentials"));
    }

    @Override
    public TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetReply) {
        Tweet parentTweet = getTweet(id);
        if (parentTweet.isDeleted()) {
            throw new BadRequestException("Tweet you are replying to is deleted");
        }

        if (tweetReply.getContent() == null) {
            throw new BadRequestException("Reply tweet needs to have content, content is not optional");
        }

        Tweet replyTweet = new Tweet();
        User author = validateCredentials(credentialsMapper.dtoToEntity(tweetReply.getCredentials()));
        UserResponseDto user = userMapper.entityToDto(author);
        System.out.println(user.getUsername());

        if (tweetReply.getContent() != null) {
            String processedContent = processContent(tweetReply.getContent());
            replyTweet.setContent(processedContent);
        }

        Tweet re = saveTweet(replyTweet, author.getCredentials());
        re.setAuthor(author);
        re.setInReplyTo(parentTweet);

        return tweetMapper.entityToDto(tweetRepository.saveAndFlush(re));
    }

    @Override
    public List<TweetResponseDto> getRepliesToTweet(Long id) {
        Tweet parentTweet = getTweet(id);
        return tweetMapper.entitiesToDtos(tweetRepository.findByInReplyTo(parentTweet));
    }

    @Override
    public void likeTweet(Long id, CredentialsDto credentials) {
        Tweet tweet = getTweet(id);
        User user = getUserByCredentials(credentialsMapper.dtoToEntity(credentials));

        if (!user.getLikedTweets().contains(tweet)) {
            user.getLikedTweets().add(tweet);
            tweet.getLikedBy().add(user);

            tweetRepository.saveAndFlush(tweet);
            userRepository.saveAndFlush(user);
        }
    }

    @Override
    public List<UserResponseDto> getLikesOnTweet(Long id) {
        Tweet tweet = getTweet(id);
        if (tweet.isDeleted()) {
            throw new NotFoundException("Tweet has been deleted or cannot be found");
        }

        List<User> likedUsers = tweet.getLikedBy().stream().filter(user -> !user.isDeleted()).toList();

        return userMapper.entitiesToDtos(userRepository.saveAllAndFlush(likedUsers));
    }

    @Override
    public TweetResponseDto repostTweet(Long id, CredentialsDto credentials) {
        Tweet originalTweet = getTweet(id);

        User user = getUserByCredentials(credentialsMapper.dtoToEntity(credentials));

        if (originalTweet.getContent() == null) {
            throw new IllegalArgumentException("Cannot repost a tweet with content");
        }

        Tweet repostTweet = new Tweet();
        repostTweet.setRepostOf(originalTweet);

        Tweet savedRepost = saveTweet(repostTweet, user.getCredentials());
        savedRepost.setAuthor(user);

        return tweetMapper.entityToDto(tweetRepository.saveAndFlush(savedRepost));
    }

    @Override
    public List<TweetResponseDto> getRepostsOnTweet(Long id) {
        Tweet tweet = getTweet(id);
        //System.out.println("Content of reposts");
        if (tweet.getReposts() == null) {
            throw new NotFoundException("This tweet has no reposts.");
        }
        //System.out.println(tweet.getRepostOf().getContent());

        List<Tweet> reposts = tweet.getReposts().stream().filter(repost -> !repost.isDeleted()).collect(Collectors.toList());
        return tweetMapper.entitiesToDtos(reposts);
    }

    @Override
    public List<UserResponseDto> getMentionsInTweet(Long id) {
        Tweet tweet = getTweet(id);
        List<User> mentionedUsers = tweet.getMentionedUsers();
        return userMapper.entitiesToDtos(mentionedUsers);
    }

    @Override
    public List<HashtagDto> getTagsOnTweet(Long id) {
        Tweet tweet = getTweet(id);
        if (tweet.isDeleted()) {
            throw new NotFoundException("Tweet has been deleted or cannot be found");
        }

        List<Hashtag> hashtags = tweet.getHashtags();
        List<HashtagDto> hashtagResponseDtos = new ArrayList<>();
        for (Hashtag hashtag : hashtags) {
            HashtagDto hashtagResponseDto = new HashtagDto();
            hashtagResponseDto.setLabel(hashtag.getLabel());
            hashtagResponseDtos.add(hashtagResponseDto);

        }
        return hashtagResponseDtos;
    }


    
    // private final TweetRepository tweetRepository;
    // private final TweetMapper tweetMapper;
    // private final CredentialsMapper credentialsMapper;
    // private final UserRepository userRepository;
    // private final HashtagRepository hashtagRepository;

    // private User getUserByUsername (String username) {
    //     Optional<User> maybeUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
    //     if (maybeUser.isEmpty())
    //         throw new NotFoundException("username does not match existing user");
    //     return maybeUser.get();
    // }

    // private User getUserByCredentials (Credentials credentials) {
    //     Optional<User> maybeUser = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(credentials.getUsername(), credentials.getPassword());
    //     if (maybeUser.isEmpty())
    //         throw new NotFoundException("credentials do not match existing user");
    //     return maybeUser.get();
    // }
    // private List<User> parseTweetMentions (Tweet tweet) {
    //     List<User> mentioned = new LinkedList<User>();
    //     String[] words = tweet.getContent().split(" ");
    //     for (String s : words)
    //         if (words.length > 1 && s.charAt(0) == '@') {
    //             String username = s.substring(1);
    //             User user = getUserByUsername(username);
    //             if (!mentioned.contains(user))
    //                 mentioned.add(user);
    //         }
    //     return mentioned;
    // }

    // private Hashtag getHashtag (String hashtagLabel, Tweet tweet) {
    //     Optional<Hashtag> maybeHashtag = hashtagRepository.findByLabel(hashtagLabel);
    //     Hashtag hashtag;
    //     if (maybeHashtag.isEmpty()) {
    //         hashtag = new Hashtag();
    //         hashtag.setLabel(hashtagLabel);
    //     }
    //     else
    //         hashtag = maybeHashtag.get();

    //     return hashtagRepository.saveAndFlush(hashtag);
    // }

    // private List<Hashtag> parseTweetHashtags (Tweet tweet) {
    //     List<Hashtag> hashtags = new LinkedList<Hashtag>();
    //     String[] words = tweet.getContent().split(" ");
    //     for (String s : words)
    //         if (words.length > 1 && s.charAt(0) == '#') {
    //             String hashtagLabel = s.substring(1);
    //             Hashtag hashtag = getHashtag(hashtagLabel, tweet);
    //             if (!hashtags.contains(hashtag))
    //                 hashtags.add(hashtag);
    //         }
    //     return hashtags;
    // }

    // private Tweet saveTweet (Tweet tweet, Credentials credentials) {
    //     tweet.setAuthor(getUserByCredentials(credentials));
    //     tweet.setMentionedUsers(parseTweetMentions(tweet));
    //     tweet.setHashtags(parseTweetHashtags(tweet));
    //     return tweetRepository.saveAndFlush(tweet);
    // }

    // @Override
    // public List<TweetResponseDto> getAllTweets () {
    //     return tweetMapper.entitiesToDtos(tweetRepository.findAllByDeletedFalseOrderByPostedDesc());
    // }

    // @Override
    // public TweetResponseDto postTweet (TweetRequestDto request) {
    //     Credentials credentials = credentialsMapper.dtoToEntity(request.getCredentials());
    //     Tweet tweet = tweetMapper.dtoToEntity(request);
    //     return tweetMapper.entityToDto(saveTweet(tweet, credentials));
    // }
}
