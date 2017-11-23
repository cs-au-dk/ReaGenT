import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {CatalogComponent} from "./catalog/catalog.component";

const routes: Routes = [
  { path: '', redirectTo: '/catalog', pathMatch: 'full' },
  { path: 'catalog',     component: CatalogComponent },
];

@NgModule({
  imports: [ RouterModule.forRoot(routes, { useHash: true }) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
