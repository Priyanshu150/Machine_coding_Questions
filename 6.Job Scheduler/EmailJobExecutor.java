

class EmailJobExecutor implements JobExecutor {
    @Override
    public void execute(Job job) {
        System.out.println(
            "Executing email job: " + job.getName()
        );
    }
}