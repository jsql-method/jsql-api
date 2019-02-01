package pl.jsql.api.dto

class CompanyRequest {

    String name
    String street
    String city
    String postalCode
    String country
    Boolean isLicensed


    CompanyRequest() {

    }

    CompanyRequest(String name, String street, String city, String postalCode, String country) {
        this.name = name
        this.street = street
        this.city = city
        this.postalCode = postalCode
        this.country = country
    }

    CompanyRequest(String name, String street, String city, String postalCode, String country, Boolean isLicensed) {
        this.name = name
        this.street = street
        this.city = city
        this.postalCode = postalCode
        this.country = country
        this.isLicensed = isLicensed
    }
}
