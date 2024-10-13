#Opinion Mining
#import Library
import pandas as pd
from nltk.sentiment.vader import SentimentIntensityAnalyzer
import json
import numpy as np

import nltk

#nltk.download('vader_lexicon')


def show_passed_array(passed_array):
    while not nltk.download('vader_lexicon'):
        print("Retrying download vader_lexicon")
    b = np.array(passed_array).tolist()
    #b = a.tolist()
    reviews = pd.DataFrame (b, columns = ['review'])
    #call the function
    sia = SentimentIntensityAnalyzer()
    #apply sia and transform them into the dataframe
    reviews['neg']=reviews['review'].apply(lambda x:sia.polarity_scores(x)['neg'])
    reviews['neu']=reviews['review'].apply(lambda x:sia.polarity_scores(x)['neu'])
    reviews['pos']=reviews['review'].apply(lambda x:sia.polarity_scores(x)['pos'])
    reviews['compound']=reviews['review'].apply(lambda x:sia.polarity_scores(x)['compound'])
    pos_review = [j for i,j in enumerate(reviews['review'])if reviews['compound'][i] > 0.2]
    neu_review = [j for i,j in enumerate(reviews['review'])if 0.2 >= reviews['compound'][i] >= -0.2]
    neg_review = [j for i,j in enumerate(reviews['review'])if reviews['compound'][i] < -0.2]
    #print("Percentage of positive review:{}%".format(len(pos_review)*100/len(reviews['review'])))
    #print("Percentage of neutral review:{}%".format(len(neu_review)*100/len(reviews['review'])))
    #print("Percentage of negative review:{}%".format(len(neg_review)*100/len(reviews['review'])))
    positive_Reviews_percentage_checker = float((len(pos_review)*100/len(reviews['review'])))
    positive_Reviews_percentage = str(round((len(pos_review)*100/len(reviews['review'])),2))
    negative_Reviews_percentage = str(round((len(neg_review)*100/len(reviews['review'])),2))
    neutral_Reviews_percentage = str(round((len(neu_review)*100/len(reviews['review'])),2))
    marquee_stats = positive_Reviews_percentage + ", " +neutral_Reviews_percentage + ", " +negative_Reviews_percentage
    if positive_Reviews_percentage_checker >= 60: #60% positive comments [our merit to show marquees that have above >= 60% positive feedbacks]
        #print(positive_Reviews_percentage)
        return marquee_stats
        #return 1 [marquee satisfies our criteria]
    else:
        return 0
    
#cars = ["bad marquee with bad staff behavior and dirty environment", "one of the worst marquee I have ever went to", "amazing marquee with best food quality", "worst marquee ever","great marquee with good staff behaviour and clean environment"]
#print(show_passed_array(cars))



    
    
    






