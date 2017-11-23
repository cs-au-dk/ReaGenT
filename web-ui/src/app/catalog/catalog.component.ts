import {Component} from '@angular/core';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/mergeMap';
import 'rxjs/add/operator/map';
import {ActivatedRoute} from "@angular/router";
import {DomSanitizer} from "@angular/platform-browser";
import {DataService} from "../services/data.service";
import {ResultIndex} from "../datastructures/results";

@Component({
  selector: 'file',
  templateUrl: './catalog.component.html',
  styleUrls: [ './catalog.component.css' ]
})
export class CatalogComponent {
    index: Observable<ResultIndex>;
    indexEntries: Observable<[string, string][]>;

    constructor(private route: ActivatedRoute,
                private dataService: DataService,
                private sanitizer: DomSanitizer) {
        this.index = this.dataService.getIndex();
        this.indexEntries = this.index.map(idx => {
            let ret = [];
            for (let k in idx.index) {
                ret.push([k, idx.index[k]]);
            }
            return ret;
        });
    }

}
