-- Place your queries here. Docs available https://www.hugsql.org/

-- :name create-gif! :<!
-- :doc inserts and returns a gif
insert into gifs(html, name)
values (:html, :name)
returning *;

-- :name get-gif-by-id :? :1
-- :doc gets a single gif given its ID
select *
from gifs
where id = :id;

-- :name list-gifs
-- :doc lists all gifs
select *
from gifs;