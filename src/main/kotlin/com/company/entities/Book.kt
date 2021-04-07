package com.company.entities

import javax.persistence.*


@Entity
@Table(name = "books")
data class Book(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int?,
    @Column(name = "title")
    var title: String = "",
    @Column(name = "author")
    var author: String = ""
) {
    constructor() : this(null, "", "")
}