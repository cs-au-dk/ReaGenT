import {Http} from "@angular/http";
import {Injectable} from "@angular/core";
import {Observable} from 'rxjs/Observable';
import {ResultIndex, ResultSummary} from "../datastructures/results";

@Injectable()
export class DataService {
    private headers = new Headers({'Content-Type': 'application/json'});
    private outUrl = 'out/';
    private testUrl = 'test/';

    constructor(private http: Http) {
    }

    getIndex(): Observable<ResultIndex> {
        return this.http.get(this.outUrl + "index.json").map(response => {
            return response.json() as ResultIndex;
        });
    }

    getResultSummary(path: string): Observable<ResultSummary> {
        return this.http.get(this.testUrl + path).map(response => {
            return response.json() as ResultSummary;
        });
    }
}
