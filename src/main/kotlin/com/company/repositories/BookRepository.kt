package com.company.repositories

import com.company.repositories.interfaces.IBookRepository
import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession
import io.micronaut.spring.tx.annotation.Transactional
import com.company.entities.Book
import javax.inject.Singleton
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

import javax.validation.constraints.NotNull

@Singleton
open class BookRepository(
    @PersistenceContext @CurrentSession var entityManager: EntityManager
) : IBookRepository {

    @Transactional
    override fun save(@NotNull book: Book): Book {
        if (book.id == null) {
            entityManager.persist(book)
        } else {
            entityManager.merge(book)
        }
        return book
    }

    @Transactional
    override fun delete(@NotNull id: Int) {
        val book = entityManager.find(Book::class.java, id)
        if (book != null) {
            entityManager.remove(book)
        }
    }

    @Transactional(readOnly = true)
    override fun findByTitle(title: String): List<Book> {
        val cb = entityManager.criteriaBuilder
        val q = cb.createQuery(Book::class.java)
        val root = q.from(Book::class.java)
        val param = cb.parameter(String::class.java)
        q.select(root).where(cb.like(root.get("title"), param))

        return entityManager.createQuery(q).setParameter(param, """%$title%""").resultList
    }

    @Transactional(readOnly = true)
    override fun findByAuthor(author: String): List<Book> {
        val qs = "select b from Book b where b.author like :author"
        return entityManager.createQuery(qs, Book::class.java).setParameter("author", """%$author%""").resultList
    }

    @Transactional(readOnly = true)
    override fun getAll(): List<Book> {
        return entityManager.createQuery(
            "select b from Book b",
            Book::class.java
        ).resultList
    }


}