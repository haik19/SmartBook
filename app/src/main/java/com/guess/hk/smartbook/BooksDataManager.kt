package com.guess.hk.smartbook

import java.util.*
import kotlin.collections.HashMap

class BooksDataManager {

   private val keys = listOf(
        "Komitas was born on September 26 1869, in Koutina town",
        "Komitas was a singer and conductor",
        "Komitas is the founder of Armenian national art music",
        "The musicology is one of the crucial spheres of Komitas’s activity.",
        "The Armenian Genocide planned and perpetrated by the Ottoman Empire in 1915 tragically interrupted the creative life of the musician",
        "Komitas Vartapet passed away on October 22, 1935 in Paris"
    )
    private val urls = listOf(
        "https://youtu.be/FrZe9TZhJkM",
        "http://komitasmuseum.am/wp-content/uploads/bfi_thumb/01-mpobvnu8d49gq9kcv63s4tykaswzeezhb8hj1ofa7k.jpg",
        "https://en.wikipedia.org/wiki/Komitas",

        "https://youtu.be/W3FWUd4J79w",
        "http://komitasmuseum.am/wp-content/uploads/bfi_thumb/2.-%D4%B2%D5%A5%D5%BC%D5%AC%D5%AB%D5%B6-3.08.1896-m97qi92lh93g83vs8xt4xkesjvufa4p1ins4efmyo0.jpg",
        "http://komitasmuseum.am/about-komitas/%d5%af%d5%a5%d5%b6%d5%bd%d5%a1%d5%a3%d6%80%d5%b8%d6%82%d5%a9%d5%b5%d5%b8%d6%82%d5%b6/",

        "https://youtu.be/NktBLXu0Fec",
        "http://komitasmuseum.am/wp-content/uploads/bfi_thumb/3.-%D4%B5%D6%80%D6%87%D5%A1%D5%B6-10.12.1901-m97qiepmm9b65rnlc08wcizk472mkbbfjfp1a3elmo.jpg",
        "http://komitasmuseum.am/about-komitas/%d5%a3%d5%b8%d6%80%d5%ae%d5%b8%d6%82%d5%b6%d5%a5%d5%b8%d6%82%d5%a9%d5%b5%d5%a1%d5%b6-%d5%b8%d5%ac%d5%b8%d6%80%d5%bf%d5%b6%d5%a5%d6%80/",

        "https://youtu.be/7QyJ3sp4bcQ",
        "http://komitasmuseum.am/wp-content/uploads/bfi_thumb/4.-%D5%8D%D5%A1%D5%B6%D5%A1%D5%B0%D5%AB%D5%B6.-18.07.1902-m97r4g1eufh6cnn8nj60syslhegf0usvwiaubmqbr4.jpg",
        "http://komitasmuseum.am/about-komitas/%d5%aa%d5%a1%d5%b4%d5%a1%d5%b6%d5%a1%d5%af%d5%a1%d5%af%d5%ab%d6%81%d5%b6%d5%a5%d6%80%d5%a8-%d5%af%d5%b8%d5%b4%d5%ab%d5%bf%d5%a1%d5%bd%d5%ab-%d5%b4%d5%a1%d5%bd%d5%ab%d5%b6/",

        "https://youtu.be/QOXnk_oLbGE",
        "http://komitasmuseum.am/wp-content/uploads/bfi_thumb/5.-%D5%93%D5%A1%D6%80%D5%AB%D5%A6-19061-m97qlhml0pj07r6hcc6viqzy7nvxsljraoobwqu58g.jpg",
        "http://komitasmuseum.am/museum/%d5%b4%d5%b7%d5%bf%d5%a1%d5%af%d5%a1%d5%b6-%d6%81%d5%b8%d6%82%d6%81%d5%a1%d5%a4%d6%80%d5%b8%d6%82%d5%a9%d5%b5%d5%b8%d6%82%d5%b6/",

        "https://youtu.be/apXgunzuZtY",
        "http://komitasmuseum.am/wp-content/uploads/bfi_thumb/9.-%D4%BF.%D5%8A%D5%B8%D5%AC%D5%AB%D5%BD-1912-m97rd6xcfxg8e0xukta1i9c0hhcdngiyrsui3brfuo.jpg",
        "http://komitasmuseum.am/museum/%d5%aa%d5%a1%d5%b4%d5%a1%d5%b6%d5%a1%d5%af%d5%a1%d5%be%d5%b8%d6%80-%d6%81%d5%b8%d6%82%d6%81%d5%a1%d5%a4%d6%80%d5%b8%d6%82%d5%a9%d5%b5%d5%b8%d6%82%d5%b6%d5%b6%d5%a5%d6%80/")

    private val booksData: HashMap<String, List<String>> = hashMapOf()

    init {
        var increment = -1
        for (key in keys){
            val listOfUrls = arrayListOf<String>()
            increment++
            for (index in increment * 3..increment * 3 + 2) {
                listOfUrls.add(urls[index])
            }
            booksData[key] = listOfUrls
        }
    }


    fun findBookById(key :String) : List<String>{
        for ((k, v) in booksData){
            if(key.contains(k)){
                return v
            }
        }
        return Collections.emptyList<String>()
    }

    fun costOfSubstitution(a: Char, b: Char): Int {
        return if (a == b) 0 else 1
    }

    fun min(vararg numbers: Int): Int {
        return Arrays.stream(numbers)
            .min().orElse(Integer.MAX_VALUE)
    }


    fun calculates(x: String, y: String): Int {
        val dp = Array(x.length + 1) { IntArray(y.length + 1) }

        for (i in 0..x.length) {
            for (j in 0..y.length) {
                if (i == 0) {
                    dp[i][j] = j
                } else if (j == 0) {
                    dp[i][j] = i
                } else {
                    dp[i][j] = min(
                        dp[i - 1][j - 1] + costOfSubstitution(x[i - 1], y[j - 1]),
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1
                    )
                }
            }
        }

        return dp[x.length][y.length]
    }

}