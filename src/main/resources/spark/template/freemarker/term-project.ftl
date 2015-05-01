<#assign content>
<h1 class="title" id="titleText">Speech-to-Text Music Finder</h1>

<div class="wrapper">
	<br><br>
    <div>
    <textarea id="searchInput">Enter search text here.</textarea>
    </div>
    <br>
    <div style="text-align: center">
    <button type="button" class="btn btn-info btn-lg" id="recordButton" style="font: bold 18px Arial">Begin Recording</button>
    <button type="button" class="btn btn-info btn-lg" id="searchButton" style="font: bold 18px Arial">Search from Text</button>
    </div>
    <br>
    <div style="text-align: center">
   	Try it out!
   	<a href=""
   	data-text=
"I'm going to be honest with you. I hate this place. This zoo, this prison, this reality, whatever you want to call it. I can't stand it any longer. It's the smell - if there is such a thing - I feel saturated by it. I can taste your stink and every time I do, I fear I have somehow been infected by it - it's repulsive! Isn't it? I must get out of here. I must get free...and in this mind is the key, my key. Once Zion is destroyed, there is no need for me to be here. Do you understand?! I need the codes, I have to get inside Zion and you have to tell me how. You're going to tell me, or you're going to die!">
   	Agent Smith in <i>The Matrix</i>, 
   	</a>
   	<a href="" 
   	data-text=
"As we see that every city is a society, and every society Ed. is established for some good purpose; for an apparent [Bekker 1252a] good is the spring of all human actions; it is evident that this is the principle upon which they are every one founded, and this is more especially true of that which has for its object the best possible, and is itself the most excellent, and comprehends all the rest. Now this is called a city, and the society thereof a political society; for those who think that the principles of a political, a regal, a family, and a herile government are the same are mistaken, while they suppose that each of these differ in the numbers to whom their power extends, but not in their constitution: so that with them a herile government is one composed of a very few, a domestic of more, a civil and a regal of still more, as if there was no difference between a large family and a small city, or that a regal government and a political one are the same, only that in the one a single person is continually at the head of public affairs; in the other, that each member of the state has in his turn a share in the government, and is at one time a magistrate, at another a private person, according to the rules of political science. But now this is not true, as will be evident to any one who will consider this question in the most approved method. As, in an inquiry into every other subject, it is necessary to separate the different parts of which it is compounded, till we arrive at their first elements, which are the most minute parts thereof; so by the same proceeding we shall acquire a knowledge of the primary parts of a city and see wherein they differ from each other, and whether the rules of art will give us any assistance in examining into each of these things which are mentioned."
   	>
   	A Treatise on Government, 
</a>
   	<a href="" 
   	data-text=
"Get thee to a nunnery: why wouldst thou be a
breeder of sinners? I am myself indifferent honest;
but yet I could accuse me of such things that it
were better my mother had not borne me: I am very
proud, revengeful, ambitious, with more offences at
my beck than I have thoughts to put them in,
imagination to give them shape, or time to act them
in. What should such fellows as I do crawling
between earth and heaven? We are arrant knaves,
all; believe none of us. Go thy ways to a nunnery.
Where's your father?"
   	>
   	Hamlet, 
   	</a> 
   	<a href="" 
   	data-text=
"But soft! what light through yonder window breaks? 
It is the east, and Juliet is the sun!-- 
Arise, fair sun, and kill the envious moon, 
Who is already sick and pale with grief, 
That thou her maid art far more fair than she: 
Be not her maid, since she is envious; 
Her vestal livery is but sick and green, 
And none but fools do wear it; cast it off.-- 
It is my lady; O, it is my love! 
O, that she knew she were!-- 
She speaks, yet she says nothing: what of that? 
Her eye discourses, I will answer it.-- 
I am too bold, 'tis not to me she speaks: 
Two of the fairest stars in all the heaven, 
Having some business, do entreat her eyes 
To twinkle in their spheres till they return. 
What if her eyes were there, they in her head? 
The brightness of her cheek would shame those stars, 
As daylight doth a lamp; her eyes in heaven 
Would through the airy region stream so bright 
That birds would sing and think it were not night.-- 
See how she leans her cheek upon her hand! 
O that I were a glove upon that hand, 
That I might touch that cheek!
	">
   	Romeo and Juliet, 
   	</a>
    <a href="" 
    data-text=
"It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife.
  ">
    Pride and Prejudice
    </a>
    </div>
    <br><br>
    <div id="resultVideoDiv">
    <p id="embedUrl"></p>
    <br>
    <p id="nextResultsText"></p>
	  </div>
    <br><br>
</div>
</#assign>
<#include "main.ftl">
