#!/bin/bash
# Coverage Analysis Script for Alliance
# Extracts coverage data from JaCoCo XML reports

echo "=== Alliance Test Coverage Analysis ==="
echo ""

# Find all jacoco.xml files
JACOCO_FILES=$(find . -name "jacoco.xml" -type f)

if [ -z "$JACOCO_FILES" ]; then
    echo "ERROR: No jacoco.xml files found."
    echo "Run: mvn clean test jacoco:report -DskipITs=true"
    exit 1
fi

echo "Found $(echo "$JACOCO_FILES" | wc -l) JaCoCo reports"
echo ""

# Create output file
OUTPUT="docs/testing/COVERAGE-HEAT-MAP.md"
echo "# Test Coverage Heat Map" > "$OUTPUT"
echo "" >> "$OUTPUT"
echo "**Generated:** $(date)" >> "$OUTPUT"
echo "**Source:** JaCoCo XML reports" >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "## Overall Coverage by Module" >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "| Module | Path | Instruction % | Branch % | Complexity % | Status |" >> "$OUTPUT"
echo "|--------|------|---------------|----------|--------------|--------|" >> "$OUTPUT"

# Process each jacoco.xml file
for file in $JACOCO_FILES; do
    # Extract module path
    MODULE_PATH=$(dirname "$file" | sed 's|^\./||' | sed 's|/target/site/jacoco||')

    # Extract module name (last component of path)
    MODULE_NAME=$(echo "$MODULE_PATH" | awk -F'/' '{print $NF}')

    # Skip if no module name
    [ -z "$MODULE_NAME" ] && continue

    # Extract coverage data using XML parsing
    # Look for the <counter> elements
    INSTRUCTION=$(grep -o '<counter type="INSTRUCTION"[^>]*' "$file" | \
                  head -1 | \
                  grep -o 'missed="[^"]*" covered="[^"]*"' | \
                  awk -F'"' '{missed=$2; covered=$4; total=missed+covered; if(total>0) print int(covered*100/total)}')

    BRANCH=$(grep -o '<counter type="BRANCH"[^>]*' "$file" | \
             head -1 | \
             grep -o 'missed="[^"]*" covered="[^"]*"' | \
             awk -F'"' '{missed=$2; covered=$4; total=missed+covered; if(total>0) print int(covered*100/total)}')

    COMPLEXITY=$(grep -o '<counter type="COMPLEXITY"[^>]*' "$file" | \
                 head -1 | \
                 grep -o 'missed="[^"]*" covered="[^"]*"' | \
                 awk -F'"' '{missed=$2; covered=$4; total=missed+covered; if(total>0) print int(covered*100/total)}')

    # Determine status
    if [ "$INSTRUCTION" -lt 70 ] || [ "$BRANCH" -lt 65 ] || [ "$COMPLEXITY" -lt 70 ]; then
        STATUS="🔴 Critical"
    elif [ "$INSTRUCTION" -lt 75 ] || [ "$BRANCH" -lt 75 ] || [ "$COMPLEXITY" -lt 75 ]; then
        STATUS="⚠️ Below Baseline"
    elif [ "$INSTRUCTION" -ge 90 ] && [ "$BRANCH" -ge 90 ] && [ "$COMPLEXITY" -ge 90 ]; then
        STATUS="✅ Excellent"
    else
        STATUS="✅ Good"
    fi

    # Add to table
    echo "| $MODULE_NAME | \`$MODULE_PATH\` | ${INSTRUCTION}% | ${BRANCH}% | ${COMPLEXITY}% | $STATUS |" >> "$OUTPUT"
done

echo "" >> "$OUTPUT"
echo "## Status Legend" >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "- 🔴 **Critical**: Instruction < 70%, Branch < 65%, or Complexity < 70%" >> "$OUTPUT"
echo "- ⚠️ **Below Baseline**: Any metric < 75%" >> "$OUTPUT"
echo "- ✅ **Good**: All metrics ≥ 75%" >> "$OUTPUT"
echo "- ✅ **Excellent**: All metrics ≥ 90%" >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "## Next Steps" >> "$OUTPUT"
echo "" >> "$OUTPUT"
echo "1. Review modules marked as 🔴 Critical or ⚠️ Below Baseline" >> "$OUTPUT"
echo "2. Prioritize security-critical modules for immediate improvement" >> "$OUTPUT"
echo "3. Refer to \`COVERAGE-IMPROVEMENT-STRATEGY.md\` for improvement guidance" >> "$OUTPUT"
echo "" >> "$OUTPUT"

echo "Coverage heat map generated: $OUTPUT"
echo ""

# Show summary statistics
echo "=== Coverage Summary ==="
CRITICAL_COUNT=$(grep "🔴 Critical" "$OUTPUT" | wc -l)
BELOW_COUNT=$(grep "⚠️ Below Baseline" "$OUTPUT" | wc -l)
GOOD_COUNT=$(grep "✅ Good" "$OUTPUT" | wc -l)
EXCELLENT_COUNT=$(grep "✅ Excellent" "$OUTPUT" | wc -l)

echo "🔴 Critical: $CRITICAL_COUNT modules"
echo "⚠️ Below Baseline: $BELOW_COUNT modules"
echo "✅ Good: $GOOD_COUNT modules"
echo "✅ Excellent: $EXCELLENT_COUNT modules"
echo ""
echo "Total modules analyzed: $((CRITICAL_COUNT + BELOW_COUNT + GOOD_COUNT + EXCELLENT_COUNT))"
