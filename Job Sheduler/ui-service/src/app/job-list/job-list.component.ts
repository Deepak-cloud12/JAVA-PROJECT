import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { JobService } from '../service/job-service';
import { Job } from '../model/job';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { WebSocketConnectionService } from '../service/websocket-connection.service';

@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.css']
})
export class JobListComponent implements OnInit, OnDestroy {
  jobs: Job[] = [];
  displayedColumns: string[] = ['name', 'status', 'duration'];
  private unsubscribe$ = new Subject<void>();

  constructor(private jobService: JobService, private webSocketService: WebSocketConnectionService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadJobs();


    this.webSocketService.socket
      .pipe(
        takeUntil(this.unsubscribe$)
      )
      .subscribe(
        (message) => {
          console.log('Received WebSocket message:', message);
          try {
            const job: Job = message; 
            this.updateJobList(job); 
            this.detectChanges(); 
          } catch (error) {
            console.error('Error parsing WebSocket message:', error);
          }
        },
        (error) => {
          console.error('WebSocket error:', error);
        },
        () => {
          console.warn('WebSocket connection closed!');
        }
      );
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  private loadJobs(): void {
    this.jobService.getJobs().subscribe(
      (jobs) => {
        this.jobs = jobs;
      },
      (error) => {
        console.error('Error fetching jobs:', error);
      }
    );
  }

  private updateJobList(job: Job): void {
    const index = this.jobs.findIndex(j => j.name === job.name);
    if (index !== -1) {
      this.jobs[index] = job; 
    } else {
      this.jobs.push(job);
    }
  }

  private detectChanges(): void {
    if (!(this.cdr as any).destroyed) {
      this.cdr.detectChanges();
    }
  }
}
