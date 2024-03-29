![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=plugin-identityimport-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-identityimport&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-identityimport)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-identityimport&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-identityimport)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-identityimport&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-identityimport)

# Plugin identityimport

## Introduction

The aim of this plugin is to import identities in batch mode to an IdentityStore server

Identities can be imported in batch with a Rest service, and they will be processed to be inserted into the IdentityStore database.

The identities will be automatically inserted, or rejected if a sibling exists, or treated manually.

The unique identifier (CUID) of the inserted identity will be returned as batch result.

## Configuration

The Identitystore endpoints must be configured, also the clients that will be authorized to use this service.

## Usage

Go to the "manage batchs" menu section.


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-identityimport/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*