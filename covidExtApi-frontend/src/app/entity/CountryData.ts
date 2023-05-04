export class CountryData {
  country: String;
  minCases: Number;
  minCasesDate: String;
  maxCases: Number;
  maxCasesDate: String;


  constructor(country: string, minCases: number, dateMin: string, maxCases: number, dateMax: string) {
    this.country = country
    this.minCases = minCases
    this.minCasesDate = dateMin
    this.maxCases = maxCases
    this.maxCasesDate = dateMax
  }
}
