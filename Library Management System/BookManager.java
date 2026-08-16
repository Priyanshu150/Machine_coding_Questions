import java.util.*

class BookManager{
    private final Map<String, Book> books;

    public BookManager(){
        books = new HashMap<>();
    }

    public boolean addBook(Book book) {
        String isbn = book.getISBN();

        if (books.containsKey(isbn)) {
            return false;
        }

        books.put(isbn, book);
        return true;
    }

    public boolean removeBook(String isbn) {
        Book book = books.get(isbn);

        if (book == null) {
            return false;
        }

        if (!book.isAvailable()) {
            return false;
        }

        books.remove(isbn);
        return true;
    }
    
    public Optional<Book> findBook(String isbn){
        Book book = books.get(isbn);
        if(book == null)
            return Optional.empty();
        
        return Optional.of(book);

        // return Optional.ofNullable(books.get(isbn));
    } 

    public void viewBooks(){
        books.values().forEach(book -> {
            System.out.println(book);
        });
    }
    
    public void displayAvailableBooks() {
        books.values()
            .stream()
            .filter(Book::isAvailable)
            .forEach(System.out::println);
    }

    public void displayBorrowedBooks() {
        books.values()
                .stream()
                .filter(book -> !book.isAvailable())
                .forEach(System.out::println);
    }
}






            
