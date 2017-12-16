package utils

import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title

object Books {

    val THE_LORD_OF_THE_RINGS_1 = Book(Isbn13("9780261102354"), Title("The Lord of the Rings 1. The Fellowship of the Ring"))
    val THE_LORD_OF_THE_RINGS_2 = Book(Isbn13("9780261102361"), Title("The Lord of the Rings 2. The Two Towers"))
    val THE_LORD_OF_THE_RINGS_3 = Book(Isbn13("9780261102378"), Title("The Lord of the Rings 3. The Return of the King"))

    val THE_DARK_TOWER_I = Book(Isbn13("9781444723441"), Title("The Dark Tower I: The Gunslinger"))
    val THE_DARK_TOWER_II = Book(Isbn13("9781444723458"), Title("The Dark Tower II: The Drawing Of The Three"))
    val THE_DARK_TOWER_III = Book(Isbn13("9781444723465"), Title("The Dark Tower III: The Waste Lands"))
    val THE_DARK_TOWER_IV = Book(Isbn13("9781444723472"), Title("The Dark Tower IV: Wizard and Glass"))
    val THE_DARK_TOWER_V = Book(Isbn13("9781444723489"), Title("The Dark Tower V: Wolves of the Calla"))
    val THE_DARK_TOWER_VI = Book(Isbn13("9781444723496"), Title("The Dark Tower VI: Song of Susannah"))
    val THE_DARK_TOWER_VII = Book(Isbn13("9781444723502"), Title("The Dark Tower VII: The Dark Tower"))

    val THE_MARTIAN = Book(Isbn13("9780091956141"), Title("The Martian"))

}