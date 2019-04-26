FROM ruby:2.5

RUN curl -sL https://deb.nodesource.com/setup_10.x | bash -
RUN apt-get update -qq && apt-get install -y zlib1g-dev libxml2-dev libxslt1-dev libffi-dev nodejs

ENV RAILS_ROOT /home/connector
RUN mkdir -p $RAILS_ROOT

WORKDIR $RAILS_ROOT

ENV RAILS_ENV='development'
ENV RACK_ENV='development'

COPY . .
RUN bundle install --without test

EXPOSE 3000
