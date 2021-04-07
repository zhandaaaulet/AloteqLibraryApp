package com.company.repositories.interfaces

import com.company.entities.Book

interface IBookRepository {
    fun save(book: Book): Book
    fun delete(id: Int)
    fun findByTitle(title: String): List<Book>
    fun findByAuthor(author: String): List<Book>
    fun getAll(): List<Book>
}