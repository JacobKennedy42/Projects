Spring Mock 01
=============================

### Summary of the API

This Discography API contains 2 entities: `Album` and `Track`. Each have an auto-generated numerical primary key. The `Album` entity has an artist, a name, and maintains a collection of `Track` entities that are part of the the `Album` using an `@OneToMany` annotation, provided by JPA, to denote the relationship between the two tables in the database. The `Track` entity has an artist (that should be the same as the album's artist), a name, a length (represents the duration of the track), and the `Album` that it belongs to annotated with `@ManyToOne` representing the opposite side of the relationship stored in the `Album` class.

An entity relationship diagram is provided below that represents the database used by the Discography API:
---

### Endpoint Documentation
