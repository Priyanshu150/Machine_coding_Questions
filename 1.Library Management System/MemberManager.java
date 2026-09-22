import java.util.*;

class MemberManager{
    private final Map<Integer, Member> members;

    public MemberManager(){
        members = new HashMap<>();
    }

    public boolean register(Member member){
        int id = member.getId();
        
        if(members.containsKey(id)){
            return false;
        }

        members.put(id, member);
        return true;
    }

    public Optional<Member> find(int id){
        return Optional.ofNullable(members.get(id));
    }
    
    public void view(){
        members.values().forEach(System.out::println);
    }
}