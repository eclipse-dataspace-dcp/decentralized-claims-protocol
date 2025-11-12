#!/bin/bash

mkdir -p HEAD
git fetch --all --tags
tags_string=$(git tag)
echo got tag string
echo $tags_string
tags_array=($tags_string)
mv * HEAD
for tag in "${tags_array[@]}"
do
  echo starting with tag $tag
  mkdir $tag
  cd $tag
  git clone $GITHUB_SERVER_URL/$GITHUB_REPOSITORY.git --depth 1 --branch ${tag} --quiet
  pwd
  mv ./decentralized-claims-protocol/* .
  cd ..
done
pwd
for dir in */; do
  if [ -f "$dir/index.html" ]; then
      echo "index exists."
      mkdir -p $dir/resources
      cp -r $dir/artifacts/src/main/resources/* $dir/resources
      rm -rf $dir/artifacts
  else
    echo "index does not exist. No copy operations"
  fi
done