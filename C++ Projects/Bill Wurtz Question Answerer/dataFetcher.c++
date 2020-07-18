//Modification of example code given here: https://curl.haxx.se/libcurl/c/htmltidy.html

#include <stdio.h>
#include <curl/curl.h>
#include <iostream>
#include <fstream>
#include <string.h>

using namespace std;

//writes data given by curl into a file via an ofstream
//@param inData : the data to be put in the file
//@param one : the number one, for some reason
//@param dataSize : the size of inData
//@param out : the filestream used to write out the data to the file.
//@return the size of the data written.
uint writeDataToFile (char* inData, size_t one, size_t dataSize, ofstream* out)
{
	(*out) << inData;
	return dataSize;
}

//like writeDataToFile, but to a string
uint writeDataToBuffer (char* inData, size_t one, size_t dataSize, string* out)
{
	if (out == nullptr)
	{
		return 0;
	}

	out->append(inData, dataSize);

	return dataSize;
}

//see if the front of the first string (represented by inCursor and in End) match the given tag
//advance the iterator
bool frontMatches (string::const_iterator& inCursor, const string::const_iterator& inEnd, const string& inTag)
{
	for (int i = 0; i < inTag.size(); ++i)
	{
		//return false if we have reached the end of the first string without fully matching it with the second string, or if a character does not match
		if (inCursor == inEnd || *inCursor != inTag[i])
		{
			return false;
		}
		++inCursor;
	}
	//all chars match
	return true;
}

//advance the cursor past the next instance of a give tag
//return true if the tag was found, false otherwise (like if the end of the string was reached)
bool skipPast (string::const_iterator& inCursor, const string::const_iterator& inEnd, const string& inTag)
{
	bool out;
	while (!(out = frontMatches(inCursor, inEnd, inTag)) && inCursor != inEnd)
	{
		++inCursor;
	}
	return out;
}

//advance the cursor to the beginning of a given tag
//return true if the tag was found, false otherwise (like if the end of the string was reached)
bool skipTo (string::const_iterator& inCursor, const string::const_iterator& inEnd, const string& inTag)
{
	bool out = skipPast (inCursor, inEnd, inTag);

	//if the tag was found, move the cursor to the start of the tag
	if (out)
	{
		inCursor -= inTag.size();
	}
	return out;
}

//return a version of the given string with all the unneeded html elements, such as &nbsp; and </br>, removed
string removeHTML (string inCopy)
{
	string::const_iterator cursor = begin(inCopy);
	string::const_iterator endCurs = end(inCopy);

	while (cursor != endCurs)
	{
		bool phraseFound = false;

		if (*cursor == '&')
		{
			//find the end of the phrase
			string::const_iterator phraseEnd = cursor;
			//if the phrase matches, remove it
			if (phraseFound = frontMatches(phraseEnd, endCurs, "&nbsp;"))
			{
				inCopy.erase(cursor, phraseEnd);
				//readjust the end cursor
				endCurs = end(inCopy);
			}
		}
		else if (*cursor == '<')
		{
			//If <A is found, erase the rest of the string, plus the 3 preceding new lines
			if (cursor != endCurs && *(cursor+1) == 'A')
			{
				phraseFound = true;
				cursor -= 3;
				inCopy.erase(cursor, endCurs);
				endCurs = cursor;
			}
			else
			{
				//find the end of the phrase
				string::const_iterator phraseEnd = cursor;
				//if the phrase matches, remove it
				if (phraseFound = frontMatches(phraseEnd, endCurs, "</br>"))
				{
					inCopy.erase(cursor, phraseEnd);
					endCurs = end(inCopy);
				}
			}
		}

		//advance the cursor if no phrase was found
		if (!phraseFound)
		{
			++cursor;
		}
	}

	return inCopy;
}

//return a string that removes the preceding and postceding spaces and tabs, and any \n's that appear (since they don't show up in the html)
string removeExcessSpace (string inCopy)
{
	//first remove all the newlines
	string::const_iterator cursor = begin(inCopy);
	string::const_iterator endCurs = end(inCopy);
	while (cursor != endCurs)
	{
		if (*cursor == '\n')
		{
			inCopy.erase(cursor);
			--endCurs;
		}
		else
		{
			++cursor;
		}
	}

	//then remove all preceding and postceding spaces and tabs
	cursor = begin(inCopy);
	endCurs = end(inCopy);
	while (cursor != endCurs && (*cursor == ' ' || *cursor == '\t'))
	{
		inCopy.erase(cursor);
		--endCurs;
	}
	cursor = end(inCopy) - 1;
	endCurs = begin(inCopy) - 1;
	while (cursor != endCurs && (*cursor == ' ' || *cursor == '\t'))
	{
		inCopy.erase(cursor);
		--cursor;
	}

	return inCopy;
}

//return the content between beginTag and endTag from the string represented by inCursor and inEnd
//advance the cursor past the end tag
string getStringBetween (string::const_iterator& inCursor, const string::const_iterator& inEnd, const string& beginTag, const string& endTag)
{

	//find the beginning and end of the string
	string::const_iterator stringBegin = inCursor;
	skipPast(stringBegin, inEnd, beginTag);
	string::const_iterator stringEnd = stringBegin;
	skipTo(stringEnd, inEnd, endTag);

	//skip the original cursor past the begin and end tags
	skipPast(inCursor, inEnd, beginTag);
	skipPast(inCursor, inEnd, endTag);

	//remove the unneeded html stuff and extra spaces
	return removeExcessSpace(removeHTML(string(stringBegin, stringEnd)));
//	return string(stringBegin, stringEnd);
}

////get the next date of a question (everything between the next instance of "<dco>" and "</dco>")
//string getNextDate (string::const_iterator& inCursor, const string::const_iterator& inEnd)
//{
//	return getStringBetween(inCursor, inEnd, "<dco>", "</dco>");
//}

//return the next question (everything between the next instance of "<qco>" and "</qco>")
string getNextQuestion (string::const_iterator& inCursor, const string::const_iterator& inEnd)
{
	//TODO: older html pages use <font ...> instead of <qco> and <dco>, need to account for that.
	return getStringBetween(inCursor, inEnd, "<qco>", "</qco>");
}

//return the next answer (everything between the next instance of "</h3>" and "<h3")
string getNextAnswer (string::const_iterator& inCursor, const string::const_iterator& inEnd)
{
	return getStringBetween(inCursor, inEnd, "</h3>", "<h3");
}

//return the next QA segment (everything between the start of the string and the next instance of "<dco>")
string getNextQASegment (string::const_iterator& inCursor, const string::const_iterator& inEnd)
{
	return getStringBetween(inCursor, inEnd, "", "<dco>");
}

//parse a QA segment and write the dates, questions, and answers to separate files
void parseQASegment (const string& inQASeg, ofstream& dFile, ofstream& qFile, ofstream& aFile)
{
	string::const_iterator cursor = begin(inQASeg);
	const string::const_iterator endCurs = end(inQASeg);

	//write the date to the date file (date starts at the start of the segment and ends at </dco>)
	dFile << getStringBetween(cursor, endCurs, "", "</dco>") << endl;

	//write out all the questions and answers to their respective files
	string question;
	string answer;
	while (cursor != endCurs)
	{
		question = getNextQuestion(cursor, endCurs);
		answer = getNextAnswer(cursor, endCurs);
		if (question.size() > 0)
		{
			qFile << question << endl;
		}
		if (answer.size() > 0)
		{
			aFile << answer << endl;
		}
	}
	//end each question and answer segment with a double newline, so we know where the end of the segment is
	qFile << endl;
	aFile << endl;
}

void parseHtmlPage (const string& inBuf)
{
	string::const_iterator cursor = begin(inBuf);
	const string::const_iterator endCurs = end(inBuf);

	ofstream dFile;
	dFile.open("dates.html");
	ofstream qFile;
	qFile.open("questions.html");
	ofstream aFile;
	aFile.open("answers.html");

	//TODO: need to remove the space(s) in front of each q and a
	//TODO: need to get rid of newlines \n, since they don't appear in the html

	//skip to the first question segment
	skipPast(cursor, endCurs, "<dco>");

	while (cursor != endCurs)
	{
//		dFile << getNextDate(cursor, endCurs) << "<br>";
//		qFile << getNextQuestion(cursor, endCurs) << "<br>\n";
//		aFile << getNextAnswer(cursor, endCurs) << "<br>\n";

		//get and parse each question segment
		string segment = getNextQASegment(cursor, endCurs);
//		cout << segment << "\n" << endl;
		parseQASegment(segment, dFile, qFile, aFile);

//		cout << string(cursor, endCurs) << endl;

		//TODO: whenever the skipPast is included, doesn't parse the last question, find out why

//		cout << getNextQASegment(cursor, endCurs) << "\n" << endl;

//		cout << getNextDate(cursor, endCurs) << endl;
//		cout << getNextQuestion(cursor, endCurs) << endl;
//		cout << getNextAnswer(cursor, endCurs) << endl;
//		cout << "******************************************************************************************" << endl;
	}

	dFile.flush();
	qFile.flush();
	aFile.flush();
	dFile.close();
	qFile.close();
	aFile.close();
}

int main(int argc, char **argv)
{
  if(argc == 2) {
    CURL *curl;
    char curl_errbuf[CURL_ERROR_SIZE];
    int err;
 
    curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_URL, argv[1]);
    curl_easy_setopt(curl, CURLOPT_ERRORBUFFER, curl_errbuf);
    curl_easy_setopt(curl, CURLOPT_NOPROGRESS, 0L);
    curl_easy_setopt(curl, CURLOPT_VERBOSE, 1L);
//    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeDataToFile);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeDataToBuffer);

//    ofstream rawFile;
//	rawFile.open("raw.html");
//	curl_easy_setopt(curl, CURLOPT_WRITEDATA, &rawFile);
	string rawBuf;
	curl_easy_setopt(curl, CURLOPT_WRITEDATA, &rawBuf);

    err = curl_easy_perform(curl);

//	rawFile.flush();
//	rawFile.close();

	//TODO
//	cout << rawBuf << endl;
	parseHtmlPage(rawBuf);

    /* clean-up */ 
    curl_easy_cleanup(curl);
    return err;
 
  }
  else
    printf("usage: %s <url>\n", argv[0]);
 
  return 0;
}
