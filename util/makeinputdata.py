#!/usr/bin/python3.8

import random

titles = 10000
title_data = (
    ("Cloud Atlas", "Warner Brothers", 4.00),
    ("Breaking Away", "Magnum Pictures", 3.00),
    ("Home Alone VII", "Buena Vista", 7.00),
    ("Chained Heat", "Low Budget Pictures", 1.25),
    ("Exorcist III", "20th Century Fox", 2.50),
    ("The Prestige", "MGM", 3.50),
    ("The Big Chill", "Warner Brothers", 2.25),
    ("Journey to the Center of the Earth", "Family Classics", 1.75),
    ("Peewee's Big Adventure", "Netflix", 0.50),
    ("The Godfather", "Capitol Pictures", 9.95)
)
stb_ids = range(50)
date_year = range(1998, 2020)
date_month = range(1, 13)
date_day = range(1, 29)
viewtime_hours = range(0, 11)
viewtime_minutes = range(0, 60, 5)

record_list = []
for x in range(0, titles):
    title_tuple = random.choice(title_data)
    record_tuple = ("stb" + str(random.choice(stb_ids)),
                    title_tuple[:2],
                    '-'.join([str(random.choice(date_year)),
                              '{:02d}'.format(random.choice(date_month)),
                              '{:02d}'.format(random.choice(date_day))]),
                    title_tuple[2:3],
                    ':'.join([str(random.choice(viewtime_hours)),
                              '{:02d}'.format(random.choice(viewtime_minutes))]))
    record_list.append(record_tuple)
tuple_list = [(a, b, c, d, '{0:.2f}'.format(e), f) for a, (b, c), d, (e,), f in
              record_list]
print("STB|TITLE|PROVIDER|DATE|REV|VIEW_TIME")
for item in tuple_list:
    print(*item, sep='|')
