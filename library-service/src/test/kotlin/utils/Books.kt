package utils

import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.Author
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title

object Books {

    private val STEPHEN_KING = Author("Stephen King")
    private val JRR_TOLKIEN = Author("J.R.R. Tolkien")
    private val ANDY_WEIR = Author("Andy Weir")
    private val GEORGE_RR_MARTIN = Author("George R.R. Martin")
    private val ROBERT_MARTIN = Author("Robert C. Martin")
    private val DEAB_WAMPLER = Author("Dean Wampler")

    val THE_LORD_OF_THE_RINGS_1 = Book(
            isbn = Isbn13("9780261102354"),
            title = Title("The Lord of the Rings 1. The Fellowship of the Ring"),
            authors = listOf(JRR_TOLKIEN),
            numberOfPages = 529
    )
    val THE_LORD_OF_THE_RINGS_2 = Book(
            isbn = Isbn13("9780261102361"),
            title = Title("The Lord of the Rings 2. The Two Towers"),
            authors = listOf(JRR_TOLKIEN),
            numberOfPages = 442
    )
    val THE_LORD_OF_THE_RINGS_3 = Book(
            isbn = Isbn13("9780261102378"),
            title = Title("The Lord of the Rings 3. The Return of the King"),
            authors = listOf(JRR_TOLKIEN),
            numberOfPages = 556
    )

    val THE_DARK_TOWER_I = Book(
            isbn = Isbn13("9781444723441"),
            title = Title("The Dark Tower I: The Gunslinger"),
            authors = listOf(STEPHEN_KING),
            numberOfPages = 304
    )
    val THE_DARK_TOWER_II = Book(
            isbn = Isbn13("9781444723458"),
            title = Title("The Dark Tower II: The Drawing Of The Three"),
            authors = listOf(STEPHEN_KING),
            numberOfPages = 496
    )
    val THE_DARK_TOWER_III = Book(
            isbn = Isbn13("9781444723465"),
            title = Title("The Dark Tower III: The Waste Lands"),
            authors = listOf(STEPHEN_KING),
            numberOfPages = 624
    )
    val THE_DARK_TOWER_IV = Book(
            isbn = Isbn13("9781444723472"),
            title = Title("The Dark Tower IV: Wizard and Glass"),
            authors = listOf(STEPHEN_KING),
            numberOfPages = 896
    )
    val THE_DARK_TOWER_V = Book(
            isbn = Isbn13("9781444723489"),
            title = Title("The Dark Tower V: Wolves of the Calla"),
            authors = listOf(STEPHEN_KING),
            numberOfPages = 816
    )
    val THE_DARK_TOWER_VI = Book(
            isbn = Isbn13("9781444723496"),
            title = Title("The Dark Tower VI: Song of Susannah"),
            authors = listOf(STEPHEN_KING),
            numberOfPages = 480
    )
    val THE_DARK_TOWER_VII = Book(
            isbn = Isbn13("9781444723502"),
            title = Title("The Dark Tower VII: The Dark Tower"),
            authors = listOf(STEPHEN_KING),
            numberOfPages = 736
    )

    val A_KNIGHT_OF_THE_SEVEN_KINGDOMS = Book(
            isbn = Isbn13("9780007507672"),
            title = Title("A Knight of the Seven Kingdoms"),
            authors = listOf(GEORGE_RR_MARTIN),
            numberOfPages = 355
    )

    val THE_MARTIAN = Book(
            isbn = Isbn13("9780091956141"),
            title = Title("The Martian"),
            authors = listOf(ANDY_WEIR),
            numberOfPages = 384
    )

    val CLEAN_CODE = Book(
            isbn = Isbn13("9780132350884"),
            title = Title("Clean Code: A Handbook of Agile Software Craftsmanship"),
            authors = listOf(ROBERT_MARTIN, DEAB_WAMPLER),
            numberOfPages = 462
    )
    val CLEAN_CODER = Book(
            isbn = Isbn13("9780137081073"),
            title = Title("Clean Coder: A Code of Conduct for Professional Programmers"),
            authors = listOf(ROBERT_MARTIN),
            numberOfPages = 256
    )

}