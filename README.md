# Crossword-with-Friends
We took inspiration from two popular games, Words with Friends, where one plays Scrabble against another player while being able to talk to that player, and Taboo, where one has to get their teammates to guess a word without explicitly using that word, to create our final project, "Crosswords with Friends". The aim of the game is to fill out the crossword puzzle with the help of your partner. One of you will be given the across clues but only allowed to fill in the down answers and the other will be given the down clues but only allowed to fill the across answers, and players are not allowed to explicitly say the clues in the chatroom. You will have to communicate with your partner to 1). relay the meaning of the clue to your partner, without saying the clue, and then 2). figure out the answer to the clue. 

#Division of Labor
Steffani was in charge of clue generation. This consisted of finding the corpus, putting the words and clues from the corpus into a sqlite database, and then retrieving that information concurrently. There were complications in finding an appropriate corpus. We, at first, were using a dictionary, using definitions as clues and the word as the answer, but then realized that dictionary definitions are not very intuitive. We moved to using a corpus that was found in a <a href='https://github.com/donohoe/nyt-crossword'>Github repository</a>. However, a lot of clues in this corpus were quite old (over 10 years old), and many were inter-dependent, so we filtered out clues that were before January 1st, 2009, for our final corpus.

Elbert was in charge of crossword generation which involved using the words and clues from the database to rearrange words in different formats until no more words could be fit in the crossword.

Melody implemented the chatroom, using web sockets, censoring out words from the clues that were important, and leaving those that aren't important, stop words. After we realized that our game was too difficult after user testing, she aslo implemented hints.

Avery took care of the front end game design and overall user experience, by actually graphically displaying the crossword. 


#Goals of the Project

We believe we met the goals of this project, which was to create a functional 2-player crossword game complete with a chatroom that censors out important words in the clues. 

