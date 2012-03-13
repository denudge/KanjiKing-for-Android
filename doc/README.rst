.. header :: *###Title###* - ###Section###

.. footer :: Page ###Page###

==============================
KanjiKing for Android - README
==============================

:Author: Mathias Lieber <mathias.lieber@googlemail.com>
:Version: 1.3.5 of 2012/03/02 

.. contents:: Table of Contents

Description
-----------
`KanjiKing for Android` is an Android application for learning Japanese characters (`Kanji`)
and vocabulary. It comes pre-packaged with 2500 Kanji and 5000 words.

It has been developed based on code of `KanjiFlashcards`_ by joe@daverin.com and I understand
this as some kind of successor as joe writes he does not maintain it anymore. So I used his
icon for example.

.. _KanjiFlashcards: http://code.google.com/p/kanji-flashcards-android/

Features
--------
* Effective cardbox system
* Order by frequency for optimal practice
* Alternating active and passive knowledge test
* Switchable between Kanji and vocabulary mode
* Pre-packaged with 2500 Kanji and 5000 words
* Simple flashcard style - you judge yourself *correct* or not
* Kanji information: Frequency, stroke count, Radical number, Hadamitzky number
* Hints based on primitives (only available in German)
* Actively used and maintained


Learning order
--------------
Learning Japanese Kanji or vocabulary means both fun and hard work, often combined with
frustration as these seem to look the same.
For teachers and students as well, it is useful to start with simpler characters
and later extend studies to more complex characters built out of simpler ones.
Unfortunately, this way you need a lot of time to master the most frequent Kanji,
that might have a high complexity but are essential to start reading public writings,
the internet or news.

`KanjiKing for Android` assumes that you have understood the concept of radicals_, as
the primitives of Japanese characters are called. The frequency of the Kanji is used
as the order of learning. This way, you can easily reach a coverage of 90% of Japanese
texts with just around 850 characters.

.. _radicals: http://en.wikipedia.org/wiki/Radical_Chinese_character

Supported languages
-------------------
The data structure for the Kanji or vocabulary files is not limited to any language.
You could add meaning in any language to a Kanji just by specifying a language tag to it.

Example

::

    <mean lang="en">tower</mean>
    <mean lang="de">Turm</mean>
    <mean lang="it">torre</mean>

Notes
-----
The data is encoded in UTF-8 and is interpreted in the Unihan method by the android device,
using a common font for JCK chars (Chinese, Japanese and Korean). That means that the Kanji
look might slightly differ from the original Japanese one. Even further, Android prefers
the Chinese variant by default and the DroidSans font in standard Android is quite Chinese.

That means that in quite some cases you have to be careful about certain strokes - but you
will get used to it. In rare cases, the chinese variant has one stroke more or less, which
you can identify by counting the strokes and comparing the count with the defined one
displayed in the next-to-top row.

Don't worry too much - most likely you won't notice the differences and your reading
capabilities won't limited at any time.

If you have the chance, you can also replace the base font in your device.

