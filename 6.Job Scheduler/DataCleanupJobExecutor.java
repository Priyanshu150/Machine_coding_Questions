
class DataCleanupJobExecutor implements JobExecutor {
    @Override
    public void execute(Job job) {
        System.out.println(
            "Executing data cleanup job: " + job.getName()
        );
    }
}