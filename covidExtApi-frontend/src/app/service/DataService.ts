import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CountryData} from "../entity/CountryData";
import {environment} from "../entity/env";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private client: HttpClient) {
  }

  getCountry(startDate: string, endDate: string, countryName: string) {
    return this.client.get<CountryData[]>(`${environment.apiUrl}/country/${countryName}?from=${startDate}&to=${endDate}`)
  }
}
