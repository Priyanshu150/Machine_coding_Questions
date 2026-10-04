import java.util.*;

class JobRepository {
    private final Map<Integer, Job> jobs;

    public JobRepository() {
        this.jobs = new HashMap<>();
    }

    public boolean addJob(Job job) {
        if (jobs.containsKey(job.getJobId())) {
            throw new IllegalArgumentException(
                "Job with ID " + job.getJobId() + " already exists"
            );
        }
        jobs.put(job.getJobId(), job);
        return true;
    }

    public boolean removeJob(int jobId) {
        return jobs.remove(jobId) != null;
    }

    Optional<Job> findJob(int jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    List<Job> getAllJobs() {
        return new ArrayList<>(jobs.values());
    }
}