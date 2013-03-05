WebCrawler
==========

This is a web crawler written in scala, and implementing several features:

1. actors: it uses actor to communicate with each other
2. each collector collects different filetypes, and this pattern allow adding new collectors quickly
3. after crawling, it sorts the result and gives the statistics. 
