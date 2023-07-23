1. What is a bid request?
   : A bid request is a message sent by an RTB Exchange or similar entity to the bidders participating in an auction for an ad impression, informing the bidders on various info relevant to prospect impression and the terms of current auction: the bid request identification, either site or application data; user and device info; geolocation; ad placement info ; content related info (including type related stuff), its producer and provider; info on deals, type of auction to name a few

2. What is a bid response?
   : A bid response is a message sent back to the RTB Exchange by every participating bidder upon getting the response request, basically it's "an offer to buy", containing bidder identification, bid price, various callback URLs, and possibly the ad markup. Alternatively, it can be a _no-bid response_

3. What is a no-bid response, and what is the format of this response?
    : A no-bid response can be treated as a response which informs the exchange of refusal by the bidder to participate in a bidding on given impression. There are multiples ways to signal no-bid response:
    
   + an empty HTTP response with _*HTTP 204 “No Content”*_ status code
   - An empty JSON object:

     `{ }`

   + A well-formed no bid response with or without a reason code:

     `{"id": "1234567890", "seatbid": [], "nbr": 2}`
         
   - According to the OpenRTB API specs, "a malformed response or a response that contains no actual bids will also be interpreted as no-bid." 

4. What is the source of the bid request (server or user)?
    :  while bid request itself is sent from an Exchange or similar entity to bidders, the chain of events starts with a human user loading
       a web page, interacting with already loaded page or running application, making the integrated SSP to send an ad request to the Exchange, causing the exchange to send bid requests to all bidding parties.  

5. What is the name of the field that refers to the time limit of the response?
    : The field `tmax` of the top-level object `BidRequest` shall contain the maximum time in milliseconds the exchange will wait for a bid response. "This value supersedes any a priori guidance from the exchange," according to the specs 

6. What is a win notification and which field is referring this data?
    : A win notice is a mean the winning bidder is notified of the auction win. Normally, it is a message, sent from the exchange with either POST or GET message, to a URL reported in `nurl` of winning `Bid` object of bid response. Also `bidid` field of `BidResponse` top-level can be reported back inside win notice. Additionally, the win notice may include other essential data that helps to identify the winning bid like the ad ID, creative ID, impression ID, etc.

7. What is the ad markup field?
    : The ad markup is  `adm` field of `BidResponse.BidSeat.Bid` sub-object, and it's a way to convey to the Exchange a document describing the ad to be served. Basically its content is opaque from a standpoint of OpenRTB specification except for macros it contains. Ad markup can be served in either `Bid` object or inside response to win notice. There's a certain trade off between these options: the later helps to save on a bandwidth, while former helps to serve the add faster, as the Exchange will be able to serve it back to the SSP right upon a win.       
 
8. What is hierarchy for the domain name in the request?


