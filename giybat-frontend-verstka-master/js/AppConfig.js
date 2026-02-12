export default class AppConfig {
    static API = window.location.hostname === 'localhost'
        ? "http://localhost:8080"
        : "http://api.giybatnoma.uz:8080";
}