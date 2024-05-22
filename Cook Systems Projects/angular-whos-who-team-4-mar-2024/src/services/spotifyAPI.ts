import { ConfigService } from "src/app/services/config.service";
import fetchFromSpotify, {request} from "./api";
import { Buffer } from 'buffer';

const AUTH_ENDPOINT = "https://nuod0t2zoe.execute-api.us-east-2.amazonaws.com/FT-Classroom/spotify-auth-token";
const TOKEN_KEY = "whos-who-access-token";
const playlistID = '5ABHKGoOzxkaa28ttQV9sE';    //ID for Top 100 Most Streamed Songs on Spotify

const getToken = async () => {
    const storedTokenString = localStorage.getItem(TOKEN_KEY);
    if (storedTokenString) {
        const storedToken = JSON.parse(storedTokenString);
        if (storedToken.expiration > Date.now()) {
            console.log("Token found in localstorage");
            return storedToken.value;
        }
    }
    console.log("Sending request to AWS endpoint");
    return await request(AUTH_ENDPOINT).then(({ access_token, expires_in }) => {
        const newToken = {
            value: access_token,
            expiration: Date.now() + (expires_in - 20) * 1000,
        };
        localStorage.setItem(TOKEN_KEY, JSON.stringify(newToken));
        return newToken.value;
    });
}

const getFilteredTracks = (items : any, explicit:boolean) => {
    return items
        .map((item : any) => item.track)
        .filter((track :any) =>
            (track.preview_url != null)
            && (explicit === true || track.explicit === false)
        )
}

export const getListOfTracks = async (explicit:boolean = false) => {
    const tracks = await getToken()
        .then(tokenValue =>
            fetchFromSpotify({token: tokenValue, endpoint: `playlists/${playlistID}`})
            .then(response => getFilteredTracks(response.tracks.items, explicit)))
    return tracks;
}