import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule }   from '@angular/forms';
import { HttpModule }    from '@angular/http';

import { AppRoutingModule } from './app-routing.module';

import { AppComponent }         from './app.component';
import {DebugPipe} from "./debug";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {CatalogComponent} from "./catalog/catalog.component";
import {DataService} from "./services/data.service";

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule,
    NgbModule.forRoot()
  ],
  declarations: [
    AppComponent,
    CatalogComponent,
    DebugPipe,
  ],
  providers: [ DataService],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
