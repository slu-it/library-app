package library.service.common

interface Mapper<in S : Any, out T : Any> {
    fun map(source: S): T
}