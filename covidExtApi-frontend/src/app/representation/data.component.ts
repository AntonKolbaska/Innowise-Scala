import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {CountryData} from "../entity/CountryData";
import {FormControl, FormGroup} from "@angular/forms";
import {MatDatepickerInputEvent} from "@angular/material/datepicker";
import {DataService} from "../service/DataService";

@Component({
  selector: 'app-data',
  templateUrl: './data.component.html',
  styleUrls: ['./data.component.css']
})
export class DataComponent implements OnChanges, OnInit {

  @Input() countryName: string = ""
  data: CountryData = {country: "", minCases: 0, minCasesDate:"", maxCases: 0, maxCasesDate: ""}
  range = new FormGroup({
    start: new FormControl<Date | null>(new Date('2020-04-01')),
    end: new FormControl<Date | null>(new Date('2020-05-01')),
  });

  constructor(private service: DataService) {
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    const startDate = this.range.controls.start.value?.toISOString().substring(0, 10);
    console.log(startDate)
    const endDate = this.range.controls.end.value?.toISOString().substring(0, 10);

    if (startDate != undefined && endDate != undefined) {
      console.log("gettin' data...");
      this.service.getCountry(startDate, endDate, this.countryName).subscribe(
        countryData => {
          this.data = countryData

          // for(var key in this.data){
          //   console.log(typeof(key))
          // }

        })

      console.log(this.data)
      console.log(this.data.maxCasesDate )
    }
  }


  startDateChange(event: MatDatepickerInputEvent<Date>) {
    if (event.value?.getFullYear() != undefined) {
      // console.log("start:");
      // console.log(event.value?.getFullYear())
      this.range.controls.start.value?.setFullYear(event.value?.getFullYear())
    }
  }

  endDateChange(event: MatDatepickerInputEvent<Date>) {
    if (event.value?.getFullYear() != undefined) {
      // console.log("end:");
      // console.log(event.value?.getFullYear())
      this.range.controls.end.value?.setFullYear(event.value?.getFullYear())
    }
  }

}
