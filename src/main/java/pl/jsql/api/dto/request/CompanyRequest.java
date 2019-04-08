package pl.jsql.api.dto.request;

public class  CompanyRequest {

    public String name;
    public String street;
    public String city;
    public String postalCode;
    public String country;
    public Boolean isLicensed;

    public CompanyRequest(String name, String street, String city, String postalCode, String country, Boolean isLicensed) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.isLicensed = isLicensed;
    }
}
