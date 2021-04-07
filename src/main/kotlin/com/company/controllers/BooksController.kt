package com.company.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import com.company.entities.Book
import com.company.repositories.interfaces.IBookRepository
import javax.inject.Inject
import javax.validation.Valid

@Controller("/library")
@Produces(MediaType.APPLICATION_JSON)
open class BooksController(
    @Inject var repository: IBookRepository
) {

    @Get("/")
    fun index(): List<Book> {
        return repository.getAll()
    }

    @Post("/")
    open fun create(@Body @Valid book: Book): HttpResponse<Book> {
        val saved = repository.save(book)
        return HttpResponse.created(saved)
    }

    @Put("/{id}")
    open fun update(@PathVariable("id") id: Int, @Body book: Book): HttpResponse<Book> {
        book.id = id;
        return HttpResponse.ok(repository.save(book))
    }

    @Delete("/{id}")
    open fun delete(@PathVariable("id") id: Int): HttpResponse<Void> {
        repository.delete(id)
        return HttpResponse.ok()
    }

    @Get("/search{?author,title}")
    open fun search(
        @QueryValue("author") author: String?,
        @QueryValue("title") title: String?
    ): HttpResponse<List<Book>> {
        val filtered = if (!author.isNullOrEmpty()) {
            repository.findByAuthor(author.orEmpty())
        } else if (!title.isNullOrEmpty()) {
            repository.findByTitle(title.orEmpty())
        } else {
            repository.getAll();
        }
        return HttpResponse.ok(filtered)
    }
}