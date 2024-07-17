import { Routes } from '@angular/router';
import { JobListComponent } from './job-list/job-list.component';
import { JobFormComponent } from './job-form/job-form.component';

export const routes: Routes = [
    { path: '', redirectTo: '/jobs', pathMatch: 'full' }, 
    { path: 'jobs', component: JobListComponent },
    { path: 'submit', component: JobFormComponent },
    { path: '**', redirectTo: '/jobs' } 
  ];
export { Routes };

