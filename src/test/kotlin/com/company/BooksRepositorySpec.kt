package com.company

import io.kotlintest.inspectors.forAll
import io.kotlintest.inspectors.forAtLeastOne
import io.kotlintest.matchers.collections.beOneOf
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.BehaviorSpec
import io.micronaut.test.annotation.MicronautTest
import com.company.entities.Book
import com.company.repositories.interfaces.IBookRepository
import javax.inject.Inject
import javax.persistence.EntityManager

@MicronautTest(application = Application::class)
class BooksRepositorySpec(
    @Inject
        var repo: IBookRepository,
    @Inject
        var em: EntityManager
): BehaviorSpec( {

    given("save book") {
        `when`("saving new book with all attributes filled") {
            val saved = repo.save(Book(null, "Book1", "Author1"))
            then("has non-null id") {
                saved.id shouldNotBe null
            }
            then ("has all other attributes") {
                saved.title shouldBe "Book1"
                saved.author shouldBe "Author1"
            }
            then ("is into the database") {
                em.find(Book::class.java, saved.id) shouldNotBe null
            }
        }
    }

    given("already saved book") {
        val saved = repo.save(Book(null, "Book1", "Author1"))

        `when`("change book title") {
            val updated = repo.save(Book(saved.id, "Book2", saved.author))

            then ("has all other attributes") {
                updated.title shouldBe "Book2"
                saved.author shouldBe "Author1"
            }
        }
    }

    given("library") {

        repo.save(Book(null, "Title1", "Author1"))
        repo.save(Book(null, "Title2", "Author24"))
        repo.save(Book(null, "Title3", "Author1"))
        repo.save(Book(null, "Title4", "Author2"))
        repo.save(Book(null, "Title5", "Author1"))
        repo.save(Book(null, "Title6", "Author3"))

        `when`("search by author Author2") {
            val books = repo.findByAuthor("thor2")

            then ("has exact 2 results") {
                books shouldHaveSize 2
            }
            then ("all the books found have author Author2") {
                books.forAll {
                    it.author shouldBe beOneOf(listOf("Author2", "Author24"))
                }
                books.forAtLeastOne { it.title shouldBe "Title2" }
                books.forAtLeastOne { it.title shouldBe "Title4" }
            }
        }

        `when`("delete book with author Author3") {
            val book = repo.findByAuthor("Author3").first()
            repo.delete(book.id!!)

            then ("there is no books with Author3 in library") {
                repo.findByAuthor("Author3") shouldHaveSize 0
            }
        }
    }

})