package Enums;

public enum CountriesEnum {

    LITHUANIA("Lithuania", "EUR", "en-LT", "lt"),
    UK("United Kingdom", "GBP", "en-GB", "com"),
    ROMANIA("Romania", "RON", "en-RO", "ro"),
    ALBANIA("Albania", "ALL", "en-AL", "al"),
    BULGARIA("Bulgaria", "BGN", "en-BG", "bg");

    CountriesEnum(String name, String currency, String url_path, String domain) {
        this.name = name;
        this.currency = currency;
        this.url_path = url_path;
        this.domain = domain;
    }

    private String name,
                   currency,
                   url_path,
                   domain;

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public String getUrl_path() {
        return url_path;
    }

    public String getDomain() {
        return domain;
    }
}
