export default class AppConfig {
    static API = window.location.hostname === 'localhost'
        ? "http://localhost:8080"
        : "http://13.60.45.252:8080";
}