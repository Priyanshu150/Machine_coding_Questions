import java.util.*;

enum BookStatus {
    AVAILABLE,
    BORROWED
}

class Book{
    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private BookStatus status;  // ENUM: AVAILABLE, BORROWED

    public Book(String isbn, String title, String author, int publicationYear){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        status = BookStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object obj){
        // equality based on ISBN
        // same object ?? 
        // it is a book 
        // compare ISBN
        if(this == obj)
            return true;
        
        if (!(obj instanceof Book other)) {
            return false;
        }

        return isbn.equals(other.isbn);
    }

    @Override
    public int hashCode(){
        // hash based on ISBN
        return isbn.hashCode();
    }
    
    public String getISBN(){
        return isbn;
    }

    public String getTitle(){
        return title;
    }

    public String getAuthor(){
        return author;
    }

    public int getPublicationYear(){
        return publicationYear;
    }

    public boolean isAvailable(){
        return (status == BookStatus.AVAILABLE);
    }

    public void borrowBook(){
        if(status == BookStatus.BORROWED){
            throw new IllegalStateException("Book is already borrowed");
        }

        status = BookStatus.BORROWED;
    }

    public void returnBook(){
        status = BookStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                ", status=" + status +
                '}';
    }
}