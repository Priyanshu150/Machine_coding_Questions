

class ReportJobExecutor implements JobExecutor {
    @Override
    public void execute(Job job) {
        System.out.println(
            "Executing report job: " + job.getName()
        );
    }
}