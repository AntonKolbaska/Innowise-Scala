export class CountryData {
  country: string;
  minCases: number;
  dateMin: string;
  maxCases: number;
  dateMax: string;


  constructor(country: string, minCases: number, dateMin: string, maxCases: number, dateMax: string) {
    this.country = country
    this.minCases = minCases
    this.dateMin = dateMin
    this.maxCases = maxCases
    this.dateMax = dateMax
  }
}
