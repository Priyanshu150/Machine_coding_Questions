
import java.time.Instant;

class Job {
    private final int jobId;
    private final String name;
    private final JobPriority priority;
    private final Instant scheduledTime;
    private int retryCount;
    private JobStatus status;   
    private final JobType type; 
    private static final int MAX_RETRIES = 3;

    public Job(int jobId, String name, JobPriority priority, Instant scheduledTime, JobType type) {
        this.jobId = jobId;
        this.name = name;
        this.priority = priority;
        this.scheduledTime = scheduledTime;
        this.type = type;
        this.retryCount = 0;
        this.status = JobStatus.SCHEDULED;
    }

    int getJobId() {
        return jobId;
    }

    JobType getType() {
        return type;
    }

    String getName() {
        return name;
    }

    JobPriority getPriority() {
        return priority;
    }

    Instant getScheduledTime() {
        return scheduledTime;
    }

    JobStatus getStatus() {
        return status;
    }

    void start(){
        if (status != JobStatus.SCHEDULED) {
            throw new IllegalStateException(
                "Only scheduled jobs can start"
            );
        }

        status = JobStatus.RUNNING;
    }
    void complete(){
        if (status != JobStatus.RUNNING) {
            throw new IllegalStateException(
                "Only running jobs can complete"
            );
        }

        status = JobStatus.COMPLETED;
    }
    void fail(){
        if (status != JobStatus.RUNNING) {
            throw new IllegalStateException(
                "Only running jobs can fail"
            );
        }

        status = JobStatus.FAILED;
    }
    void cancel(){
        if (status != JobStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled jobs can be cancelled");
        }

        status = JobStatus.CANCELLED;
    }

    void retry() {
        if (status != JobStatus.FAILED) {
            throw new IllegalStateException("Only failed jobs can be retried");
        }

        if (retryCount >= MAX_RETRIES) {
            throw new IllegalStateException("Maximum retry limit reached");
        }

        retryCount++;
        status = JobStatus.SCHEDULED;
    }
}