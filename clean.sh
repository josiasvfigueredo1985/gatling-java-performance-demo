#!/bin/bash
# Check if parameters are provided
if [ "$#" -lt 1 ]; then
  echo "Usage: $0 [--all|--results|--maven|--docker]"
  echo "Example: $0 --all"
  exit 1
fi

CLEAN_OPTION=$1

case $CLEAN_OPTION in
    --all)
        echo "Cleaning all artifacts..."
        rm -rf results/* target/
        docker-compose down --rmi local
        ./mvnw clean
        ;;
    --results)
        echo "Cleaning results directory..."
        read -p "This will delete all test results. Are you sure? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            rm -rf results/*
            echo "Results directory cleaned."
        else
            echo "Operation cancelled."
        fi
        ;;
    --maven)
        echo "Cleaning Maven artifacts..."
        rm -rf target/
        ./mvnw clean
        ;;
    --docker)
        echo "Cleaning Docker artifacts..."
        docker-compose down --rmi local
        ;;
    *)
        echo "Unknown option: $CLEAN_OPTION"
        echo "Usage: $0 [--all|--results|--maven|--docker]"
        exit 1
        ;;
esac

echo "Cleaning completed."