import type { Product } from '../types';

/**
 * Calculates Levenshtein distance between two strings
 */
function levenshteinDistance(a: string, b: string): number {
  const an = a ? a.length : 0;
  const bn = b ? b.length : 0;
  if (an === 0) return bn;
  if (bn === 0) return an;

  const matrix: number[][] = [];
  for (let i = 0; i <= bn; i++) {
    matrix[i] = [i];
  }
  for (let j = 0; j <= an; j++) {
    matrix[0][j] = j;
  }

  for (let i = 1; i <= bn; i++) {
    for (let j = 1; j <= an; j++) {
      if (b.charAt(i - 1) === a.charAt(j - 1)) {
        matrix[i][j] = matrix[i - 1][j - 1];
      } else {
        matrix[i][j] = Math.min(
          matrix[i - 1][j - 1] + 1, // substitution
          matrix[i][j - 1] + 1,     // insertion
          matrix[i - 1][j] + 1      // deletion
        );
      }
    }
  }

  return matrix[bn][an];
}

/**
 * Normalizes text for typo & symbol-tolerant comparison (removes spaces, hyphens, punctuation)
 */
function normalizeString(str: string): string {
  return str.toLowerCase().replace(/[^a-z0-9]/g, '');
}

export interface SearchResult {
  directMatches: Product[];
  similarMatches: Product[];
  isSimilarSuggestion: boolean;
}

/**
 * Performs priority-ranked search and database-only similar suggestions
 */
export function searchProducts(products: Product[], query: string): SearchResult {
  const trimmed = query.trim();
  if (!trimmed) {
    return { directMatches: products, similarMatches: [], isSimilarSuggestion: false };
  }

  const queryLower = trimmed.toLowerCase();
  const queryNormalized = normalizeString(trimmed);

  const directWithScores: { product: Product; score: number }[] = [];
  const similarWithScores: { product: Product; score: number }[] = [];

  for (const p of products) {
    const nameLower = p.name.toLowerCase();
    const skuLower = p.sku.toLowerCase();
    const nameNormalized = normalizeString(p.name);
    const skuNormalized = normalizeString(p.sku);

    // 1. Exact matches (Highest Priority)
    if (nameLower === queryLower || skuLower === queryLower) {
      directWithScores.push({ product: p, score: 100 });
      continue;
    }

    // 2. Exact normalized matches (e.g., "cocacola" vs "coca-cola")
    if (nameNormalized.startsWith(queryNormalized) && queryNormalized.length >= 4) {
      directWithScores.push({ product: p, score: 90 });
      continue;
    }

    // 3. Prefix matches
    if (nameLower.startsWith(queryLower) || skuLower.startsWith(queryLower)) {
      directWithScores.push({ product: p, score: 80 });
      continue;
    }

    // 4. Substring matches
    if (nameLower.includes(queryLower) || skuLower.includes(queryLower)) {
      directWithScores.push({ product: p, score: 70 });
      continue;
    }

    // 5. Normalized Substring (ignores hyphens and spaces e.g. "coke500" in "COKE-500")
    if (queryNormalized.length >= 3 && (nameNormalized.includes(queryNormalized) || skuNormalized.includes(queryNormalized))) {
      directWithScores.push({ product: p, score: 60 });
      continue;
    }

    // 6. Word-level prefix match (e.g., "Rice" matches "India Gate Basmati Rice 5kg")
    const words = nameLower.split(/\s+/);
    let matchedWord = false;
    for (const w of words) {
      if (w.startsWith(queryLower)) {
        directWithScores.push({ product: p, score: 50 });
        matchedWord = true;
        break;
      }
    }
    if (matchedWord) continue;

    // 7. Similar / Typo Fuzzy Matching (Only from actual database products)
    // Compare query with each word in product name and sku
    let bestDistance = 999;
    for (const w of words) {
      const cleanWord = normalizeString(w);
      if (cleanWord.length >= 3 && queryNormalized.length >= 3) {
        const dist = levenshteinDistance(cleanWord, queryNormalized);
        if (dist < bestDistance) bestDistance = dist;
      }
    }

    // Also compare whole normalized name prefix with query
    if (queryNormalized.length >= 4) {
      const namePrefix = nameNormalized.substring(0, queryNormalized.length);
      const dist = levenshteinDistance(namePrefix, queryNormalized);
      if (dist < bestDistance) bestDistance = dist;
    }

    // Tolerance: distance <= 2 for words with length >= 4, distance <= 1 for shorter
    const maxAllowedDist = queryNormalized.length >= 5 ? 2 : (queryNormalized.length >= 3 ? 1 : 0);
    if (bestDistance <= maxAllowedDist) {
      similarWithScores.push({ product: p, score: 40 - bestDistance });
    }
  }

  // Sort by score descending
  directWithScores.sort((a, b) => b.score - a.score);
  similarWithScores.sort((a, b) => b.score - a.score);

  const directMatches = directWithScores.map(item => item.product);
  const similarMatches = similarWithScores.map(item => item.product);

  if (directMatches.length > 0) {
    return {
      directMatches,
      similarMatches: [],
      isSimilarSuggestion: false,
    };
  }

  if (similarMatches.length > 0) {
    return {
      directMatches: similarMatches,
      similarMatches,
      isSimilarSuggestion: true,
    };
  }

  return {
    directMatches: [],
    similarMatches: [],
    isSimilarSuggestion: false,
  };
}
