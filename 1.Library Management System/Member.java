import java.util.*;

class Member{
    private final int memberId;
    private String name;
    private String email;
    private Set<Book> borrowedBooks;
    private static final int MAX_BORROWED_BOOKS = 3;      

    public Member(int memberId, String name, String email){
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        borrowedBooks = new HashSet<>();
    }
    
    public int getId(){
        return memberId;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String toString() {
        return "Member{" +
                "Member Id ='" + memberId + '\'' +
                ", Name ='" + name + '\'' +
                ", Email ='" + email + '\'' +
                '}';
    }

    public boolean canBorrow(){
        return borrowedBooks.size() < MAX_BORROWED_BOOKS;
    }

    public void borrowBook(Book book){
        if(!canBorrow()){
            throw new IllegalStateException("Borrow limit reached");
        }

        if (!borrowedBooks.add(book)) {
            throw new IllegalStateException("Member already borrowed this book");
        }
    }

    public boolean hasBook(Book book){
        return (borrowedBooks.contains(book));
    }

    public void returnBook(Book book){
        if (!borrowedBooks.remove(book)) {
            throw new IllegalStateException("Member has not borrowed this book");
        }
    }

    public Set<Book> getBorrowedBooks(){
        return Collections.unmodifiableSet(borrowedBooks);
    }
}