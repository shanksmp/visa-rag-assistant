# Visa RAG Assistant

A Retrieval-Augmented Generation (RAG) system that answers questions about F-1 student visa OPT/STEM OPT rules, built with Java, Spring Boot, and Spring AI. Answers are grounded in official source documents with citations, and the system explicitly declines to answer when the retrieved context doesn't contain the relevant information.

## Why this project

Most "chat with your PDF" demos are generic and interchangeable — same LangChain/Python/Streamlit stack, same shallow feature set. This project takes a different approach:

- **A real, personal use case** — built around F-1 OPT/STEM OPT rules, informed by direct experience with how confusing this process is
- **Citation-enforced answers** — every response cites its source document; the model is instructed to abstain rather than guess when context is insufficient
- **Local embeddings** — document chunking happens via Ollama running entirely on-device, so source documents never leave the machine before being embedded
- **A measured accuracy number** — a JUnit-based eval suite tests real questions against the live pipeline, not just spot-checks

## Architecture

PDF documents
↓ (chunked via Spring AI's TokenTextSplitter)
Text chunks
↓ (embedded via Ollama, nomic-embed-text — local, no API cost)
Embeddings + chunk text
↓ (stored in Postgres via pgvector)
Vector store
↓ (question embedded, similarity search, top-5 nearest chunks retrieved)
Retrieved context
↓ (assembled into a prompt with strict grounding instructions)
Claude (Anthropic API)
↓
Answer with source citation


## Tech stack

- **Java 21**, **Spring Boot 3.5.16**, **Spring AI 1.1.7**
- **Postgres + pgvector** — vector storage and similarity search
- **Ollama** (`nomic-embed-text`) — local embedding generation
- **Claude (Anthropic API)** — answer generation
- Minimal HTML/JS frontend, served as a Spring Boot static resource

## Features

- `POST /api/ingest` — ingest a PDF document into the knowledge base
- `GET /api/ask` — ask a question, get a cited, grounded answer
- Web UI at `/` for asking questions without curl/Postman
- Automated eval suite (`QuestionAnsweringEvalTest`) — runs 14 real questions against the live pipeline and reports accuracy

## Eval results

**14/14 (100%)** on the current eval set, covering eligibility rules, timing deadlines, hour requirements, cap-gap extension, and H-1B transition questions.

This number came from real debugging, not a lucky first run: an early version scored 13/14 because a rephrased duplicate question retrieved different chunks than its counterpart, missing a specific figure. Rather than hallucinate, the system correctly abstained ("the specific number is not stated in the provided context") — but that abstention revealed a retrieval consistency gap. Increasing `topK` from 3 to 5 (widening the similarity search) fixed it, confirmed by rerunning the suite.

## Setup

1. Install Postgres with the `pgvector` extension enabled
2. Install [Ollama](https://ollama.com) and pull the embedding model: `ollama pull nomic-embed-text`
3. Set your Anthropic API key as the `ANTHROPIC_API_KEY` environment variable
4. Create a database: `createdb visa_rag`
5. Run the app: the Spring Boot application will auto-initialize the pgvector schema on startup
6. Ingest a document: `POST /api/ingest?filePath=<path>&sourceName=<name>`
7. Ask a question via the web UI at `http://localhost:8080` or `GET /api/ask?question=<question>`

## What I'd add next

- Expand the document set beyond one school's OPT page to full USCIS/DHS sources
- Add retrieval precision metrics (not just answer accuracy)
- Response latency tracking