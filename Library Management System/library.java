import java.util.*

class Library{
    private final BookManager bookManager;
    private final MemberManager memberManager;

    public Library(BookManager bookManager, MemberManager memberManager){
        this.bookManager = bookManager;
        this.memberManager = memberManager;
    }

    public void borrowBook(int memberId, String isbn){
        // 1. Find member
        Optional<Member> memberOptional = memberManager.find(memberId);
        if(memberOptional.isEmpty()){
            throw new IllegalStateException("Member not found");
        }
        Member member = memberOptional.get();

        // 2. Find book
        Optional<Book> bookOptional = bookManager.findBook(isbn);

        if (bookOptional.isEmpty()) {
            throw new IllegalStateException("Book not found");
        }

        Book book = bookOptional.get();

        // 3. Validate book availability
        if(!book.isAvailable()){
            throw new IllegalStateException("Book is already borrowed");
        }
        // 4. Validate member borrowing limit
        if(!member.canBorrow()){
            throw new IllegalStateException("Borrow limit reached");
        }

        // 5. Borrow book
        book.borrowBook();

        // 6. Add book to member
        member.borrowBook(book);
    }

    public void returnBook(int memberId, String isbn){
        // find member details 
        Member member = memberManager.find(memberId)
        .orElseThrow(() -> new IllegalStateException("Member not found"));

        // find book details 
        Book book = bookManager.findBook(isbn)
        .orElseThrow(() -> new IllegalStateException("Book not found"));

        // member has book 
        if(!member.hasBook(book)){
            throw new IllegalStateException("Member doesn't have book");
        }

        // member returns book
        member.returnBook(book);

        // update the book status after returning 
        book.returnBook();
    }

    public void displayAvailableBooks(){
        bookManager.displayAvailableBooks();
    }

    public void displayAllBorrowedBooks() {
        bookManager.displayBorrowedBooks();
    }
    
    public void displayMemberBooks(int memberId){
        Member member = memberManager.find(memberId)
            .orElseThrow(() -> new IllegalStateException("Member not found"));
        
        member.getBorrowedBook().forEach(System.out::println);
    }
}