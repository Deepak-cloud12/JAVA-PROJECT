import { Component } from '@angular/core';
import { JobService } from '../service/job-service';
import { Job } from '../model/job';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-job-form',
  templateUrl: './job-form.component.html',
  styleUrls: ['./job-form.component.css']
})
export class JobFormComponent {
  job: Job = { name: '', duration: 0, status: 'pending' };

  constructor(private jobService: JobService, private snackBar: MatSnackBar) {}

  submitJob() {
    this.jobService.submitJob(this.job).subscribe(() => {
      this.snackBar.open('Job submitted successfully!', 'Close', { duration: 3000 });
      this.job = { name: '', duration: 0, status: 'pending' };
    });
  }
}
