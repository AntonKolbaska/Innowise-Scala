import {Component, OnInit} from '@angular/core';
import {Country} from "./entity/Country";
import {CountryService} from "./service/CountryService";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  countries: Country[] = []
  displayedCountries: Country[] = []
  selectedCountry: string = ""

  constructor(private service: CountryService) {
  }

  ngOnInit() {

    this.service.getAllCountries().subscribe(
      data => {
        this.countries = data.sort(
          (a, b) => (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0))
        this.displayedCountries = this.countries
        // console.log(this.displayedCountries)
      })
  }

  filterEnteredCountries(event: KeyboardEvent) {
    const filterText = (event.target as HTMLInputElement).value
    if (filterText != "") {
      this.displayedCountries = this.countries.filter(country =>
        country.name.toLowerCase().startsWith(filterText.toLowerCase()))
    }
  }

  selectCountry(event: Event) {
    this.selectedCountry = (event.target as HTMLButtonElement).id + "";
  }

}
