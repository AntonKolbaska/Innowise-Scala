import {Injectable} from "@angular/core";
import {Country} from "./entity/country";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root',
})
export class CountryService {

  constructor(private client: HttpClient) {
  }

  getAllCountries() {
    // console.log(this.client.get<Country[]>('http://localhost:8080/covidextendedapi/countries'));
    return this.client.get<Country[]>('http://localhost:8080/covidextendedapi/countries')
  }

}
