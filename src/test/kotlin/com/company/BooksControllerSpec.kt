package com.company

import io.kotlintest.*
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.specs.DescribeSpec
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MicronautTest
import com.company.entities.Book
import com.company.repositories.interfaces.IBookRepository
import javax.inject.Inject

@MicronautTest(application = Application::class)
class BooksControllerSpec(
        @Client("/library")
        @Inject
        val client: HttpClient,

        @Inject
        val repo: IBookRepository
) : DescribeSpec({

    describe("GET /") {
        val result:List<Book> = client.toBlocking().retrieve(HttpRequest.GET<Any>("/"), Argument.listOf(Book::class.java))
        it("it1") {
            result shouldHaveSize 2
        }
        it("it2") {
            val book = result.firstOrNull()
            book shouldNotBe null
            book!!.id shouldNotBe null
            book.title shouldStartWith "Book"
            book.author shouldStartWith "Author"
        }
    }

    describe("POST") {
        val book = Book(null, "New Book", "Me")
        val response: HttpResponse<Book> = client.toBlocking().exchange(HttpRequest.POST("/", book), Book::class.java)
        println(response.header("Content-Length"))
        println(response.getBody(Book::class.java))
        it ("it1") {
            response.status.code shouldBe 201
        }
        it ("it2") {
            val body = response.body()
            body shouldNotBe null
            body!!.id shouldNotBe null
            body.title shouldBe book.title
        }
    }


}) {



    override fun afterTest(testCase: TestCase, result: TestResult) {
        println("After test " + testCase.name  )
    }

    override fun beforeTest(testCase: TestCase) {
        println("Before test " + testCase.name + """ (${testCase.type.name})""")
        if (testCase.type == TestType.Container) {
            repo.save(Book(id = null, title = "Book 1", author = "Author 1"))
            repo.save(Book(id = null, title = "Book 2", author = "Author 2"))
        }
    }
}