import {Injectable} from "@angular/core";
import {Country} from "../entity/Country";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root',
})
export class CountryService {

  constructor(private client: HttpClient) {
  }

  getAllCountries() {
    return this.client.get<Country[]>('http://localhost:8080/covidextendedapi/countries')
  }

}
